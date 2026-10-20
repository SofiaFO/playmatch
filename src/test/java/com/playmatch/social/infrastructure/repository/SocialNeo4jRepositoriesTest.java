package com.playmatch.social.infrastructure.repository;

import com.playmatch.jogo.domain.AvaliacaoDoJogo;
import com.playmatch.jogo.domain.EstatisticasAvaliacao;
import com.playmatch.jogo.domain.Jogo;
import com.playmatch.jogo.infrastructure.repository.JogoRepository;
import com.playmatch.jogo.infrastructure.repository.Neo4jAvaliacaoJogoConsultaRepository;
import com.playmatch.social.domain.Atividade;
import com.playmatch.social.domain.TipoAtividade;
import com.playmatch.social.domain.UsuarioConectado;
import com.playmatch.support.Neo4jIntegrationTest;
import com.playmatch.usuario.domain.EstatisticasPerfil;
import com.playmatch.usuario.domain.Usuario;
import com.playmatch.usuario.infrastructure.repository.Neo4jPerfilRepository;
import com.playmatch.usuario.infrastructure.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.neo4j.test.autoconfigure.DataNeo4jTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.neo4j.core.Neo4jClient;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Cenário:
 * <ul>
 *     <li>Ana: avaliou Hades com 5 e resenha, favoritou Celeste, quer jogar Hollow Knight</li>
 *     <li>Bruno: avaliou Hades com 3, sem resenha</li>
 *     <li>Sofia segue Ana; Ana segue Bruno</li>
 * </ul>
 */
@DataNeo4jTest
@EnabledIf("com.playmatch.support.Neo4jIntegrationTest#dockerDisponivel")
@Import({
        Neo4jSeguidorRepository.class,
        Neo4jFeedRepository.class,
        Neo4jPerfilRepository.class,
        Neo4jAvaliacaoJogoConsultaRepository.class
})
class SocialNeo4jRepositoriesTest extends Neo4jIntegrationTest {

    @Autowired
    private Neo4jSeguidorRepository seguidorRepository;

    @Autowired
    private Neo4jFeedRepository feedRepository;

    @Autowired
    private Neo4jPerfilRepository perfilRepository;

    @Autowired
    private Neo4jAvaliacaoJogoConsultaRepository avaliacaoJogoConsultaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JogoRepository jogoRepository;

    @Autowired
    private Neo4jClient neo4jClient;

    private UUID sofia;
    private UUID ana;
    private UUID bruno;

    @BeforeEach
    void montarGrafo() {
        neo4jClient.query("MATCH (n) DETACH DELETE n").run();

        Jogo hades = salvar(1L, "Hades", List.of("Action", "Roguelike"), List.of("PC"));
        Jogo celeste = salvar(2L, "Celeste", List.of("Platformer"), List.of("PC", "Nintendo Switch"));
        Jogo hollowKnight = salvar(3L, "Hollow Knight", List.of("Action", "Platformer"), List.of("PC"));

        sofia = usuarioRepository.save(novoUsuario("Sofia")).getId();

        Usuario usuarioAna = novoUsuario("Ana");
        usuarioAna.avaliar(hades, 5, "Viciante do começo ao fim");
        usuarioAna.favoritar(celeste);
        usuarioAna.adicionarDesejo(hollowKnight);
        ana = usuarioRepository.save(usuarioAna).getId();

        Usuario usuarioBruno = novoUsuario("Bruno");
        usuarioBruno.avaliar(hades, 3, null);
        bruno = usuarioRepository.save(usuarioBruno).getId();

        seguidorRepository.seguir(sofia, ana);
        seguidorRepository.seguir(ana, bruno);
    }

    @Test
    void seguirDuasVezesNaoDuplicaARelacao() {
        seguidorRepository.seguir(sofia, ana);

        assertThat(seguidorRepository.contarSeguidores(ana)).isEqualTo(1);
        assertThat(seguidorRepository.listarSeguidores(ana, 0, 10))
                .extracting(UsuarioConectado::usuarioId)
                .containsExactly(sofia);
        assertThat(seguidorRepository.listarSeguindo(sofia, 0, 10).getFirst().desde()).isNotNull();
    }

    @Test
    void deixarDeSeguirInformaSeARelacaoExistia() {
        assertThat(seguidorRepository.deixarDeSeguir(sofia, ana)).isTrue();
        assertThat(seguidorRepository.deixarDeSeguir(sofia, ana)).isFalse();
        assertThat(seguidorRepository.contarSeguindo(sofia)).isZero();
    }

    @Test
    void feedMostraSoAtividadesDeQuemOUsuarioSegue() {
        List<Atividade> feed = feedRepository.buscarFeed(sofia, 0, 20);

        assertThat(feed).extracting(Atividade::tipo).containsExactlyInAnyOrder(
                TipoAtividade.AVALIOU,
                TipoAtividade.FAVORITOU,
                TipoAtividade.QUER_JOGAR,
                TipoAtividade.COMECOU_A_SEGUIR
        );
        assertThat(feed).allSatisfy(atividade -> assertThat(atividade.autorId()).isEqualTo(ana));
        assertThat(feed).extracting(Atividade::data)
                .isSortedAccordingTo(Comparator.reverseOrder());
        assertThat(feed).extracting(Atividade::descricao).contains(
                "Ana avaliou Hades com nota 5",
                "Ana favoritou Celeste",
                "Ana quer jogar Hollow Knight",
                "Ana começou a seguir Bruno"
        );
    }

    @Test
    void datasDoSdnEDoCypherTemOMesmoTipo() {
        // O feed ordena datas gravadas pelo SDN (Instant) junto com datas de datetime() do Cypher;
        // com tipos diferentes o Neo4j ordenaria por tipo antes de ordenar por valor.
        List<String> tipos = neo4jClient.query("""
                        MATCH ()-[a:AVALIOU]->() WITH valueType(a.criadaEm) AS tipo LIMIT 1
                        MATCH ()-[s:SEGUE]->() RETURN DISTINCT [tipo, valueType(s.desde)] AS tipos
                        """)
                .fetchAs(List.class)
                .mappedBy((tiposDriver, registro) -> registro.get("tipos").asList(valor -> valor.asString()))
                .one()
                .orElseThrow();

        assertThat(tipos.getFirst()).isEqualTo(tipos.getLast());
    }

    @Test
    void feedEhPaginado() {
        assertThat(feedRepository.buscarFeed(sofia, 0, 3)).hasSize(3);
        assertThat(feedRepository.buscarFeed(sofia, 3, 3)).hasSize(1);
    }

    @Test
    void feedDeQuemNaoSegueNinguemEhVazio() {
        assertThat(feedRepository.buscarFeed(bruno, 0, 20)).isEmpty();
    }

    @Test
    void perfilResumeAtividadeEGostosDoJogador() {
        EstatisticasPerfil perfil = perfilRepository.buscarEstatisticas(ana, sofia);

        assertThat(perfil.totalAvaliacoes()).isEqualTo(1);
        assertThat(perfil.notaMedia()).isEqualTo(5.0);
        assertThat(perfil.totalFavoritos()).isEqualTo(1);
        assertThat(perfil.totalDesejos()).isEqualTo(1);
        assertThat(perfil.seguidores()).isEqualTo(1);
        assertThat(perfil.seguindo()).isEqualTo(1);
        assertThat(perfil.generosFavoritos()).containsExactly("Action", "Platformer", "Roguelike");
        assertThat(perfil.plataformasFavoritas()).containsExactly("PC", "Nintendo Switch");
        assertThat(perfil.visitanteSegue()).isTrue();
    }

    @Test
    void perfilSemGostosDefinidosTemListasVazias() {
        EstatisticasPerfil perfil = perfilRepository.buscarEstatisticas(bruno, sofia);

        assertThat(perfil.totalAvaliacoes()).isEqualTo(1);
        assertThat(perfil.generosFavoritos()).isEmpty();
        assertThat(perfil.plataformasFavoritas()).isEmpty();
        assertThat(perfil.visitanteSegue()).isFalse();
    }

    @Test
    void estatisticasDoJogoUsamAsNotasDaComunidade() {
        assertThat(avaliacaoJogoConsultaRepository.buscarEstatisticas(1L))
                .isEqualTo(new EstatisticasAvaliacao(4.0, 2));
        assertThat(avaliacaoJogoConsultaRepository.buscarEstatisticas(2L))
                .isEqualTo(EstatisticasAvaliacao.vazia());

        Map<Long, EstatisticasAvaliacao> varios = avaliacaoJogoConsultaRepository.buscarEstatisticas(List.of(1L, 2L, 3L));
        assertThat(varios).containsOnlyKeys(1L);
    }

    @Test
    void avaliacoesDoJogoPodemSerFiltradasPorResenha() {
        List<AvaliacaoDoJogo> todas = avaliacaoJogoConsultaRepository.listarAvaliacoes(1L, false, 0, 10);
        List<AvaliacaoDoJogo> comResenha = avaliacaoJogoConsultaRepository.listarAvaliacoes(1L, true, 0, 10);

        assertThat(todas).extracting(AvaliacaoDoJogo::nomeUsuario).containsExactlyInAnyOrder("Ana", "Bruno");
        assertThat(comResenha).singleElement().satisfies(avaliacao -> {
            assertThat(avaliacao.nomeUsuario()).isEqualTo("Ana");
            assertThat(avaliacao.resenha()).isEqualTo("Viciante do começo ao fim");
            assertThat(avaliacao.criadaEm()).isNotNull();
        });
        assertThat(avaliacaoJogoConsultaRepository.contarAvaliacoes(1L, true)).isEqualTo(1);
        assertThat(avaliacaoJogoConsultaRepository.contarAvaliacoes(1L, false)).isEqualTo(2);
    }

    private Jogo salvar(Long id, String nome, List<String> generos, List<String> plataformas) {
        return jogoRepository.save(Jogo.importar(
                id, nome, null, LocalDate.of(2020, 1, 1), null, 4.0, generos, plataformas
        ));
    }

    private static Usuario novoUsuario(String nome) {
        return Usuario.builder()
                .nome(nome)
                .email(nome.toLowerCase() + "@playmatch.com")
                .senhaHash("hash")
                .build();
    }
}
