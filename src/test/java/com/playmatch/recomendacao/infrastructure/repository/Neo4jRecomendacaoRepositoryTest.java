package com.playmatch.recomendacao.infrastructure.repository;

import com.playmatch.jogo.domain.Jogo;
import com.playmatch.jogo.infrastructure.repository.JogoRepository;
import com.playmatch.recomendacao.domain.JogoRecomendado;
import com.playmatch.recomendacao.domain.UsuarioCompativel;
import com.playmatch.support.Neo4jIntegrationTest;
import com.playmatch.usuario.domain.Usuario;
import com.playmatch.usuario.infrastructure.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.neo4j.test.autoconfigure.DataNeo4jTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.neo4j.core.Neo4jClient;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Executa as queries de recomendação contra um Neo4j real (Testcontainers).
 *
 * Cenário:
 * <ul>
 *     <li>Sofia: Hades 5, Celeste 2</li>
 *     <li>Ana (mesmo gosto da Sofia): Hades 5, Celeste 2, Hollow Knight 5</li>
 *     <li>Bruno (gosto oposto): Hades 1, Celeste 5, FIFA 5</li>
 *     <li>Novato: nenhuma avaliação</li>
 * </ul>
 */
@DataNeo4jTest
@EnabledIf("com.playmatch.support.Neo4jIntegrationTest#dockerDisponivel")
@Import(Neo4jRecomendacaoRepository.class)
class Neo4jRecomendacaoRepositoryTest extends Neo4jIntegrationTest {

    @Autowired
    private Neo4jRecomendacaoRepository repository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JogoRepository jogoRepository;

    @Autowired
    private Neo4jClient neo4jClient;

    private UUID sofia;
    private UUID ana;
    private UUID bruno;
    private UUID novato;

    @BeforeEach
    void montarGrafo() {
        neo4jClient.query("MATCH (n) DETACH DELETE n").run();

        Jogo hades = salvar(1L, "Hades", 4.6, List.of("Action", "Roguelike"), List.of("PC", "Nintendo Switch"));
        Jogo deadCells = salvar(2L, "Dead Cells", 4.4, List.of("Action", "Roguelike"), List.of("PC"));
        Jogo celeste = salvar(3L, "Celeste", 4.2, List.of("Platformer"), List.of("PC", "Nintendo Switch"));
        Jogo fifa = salvar(4L, "FIFA", 3.0, List.of("Sports"), List.of("PlayStation 5"));
        Jogo hollowKnight = salvar(5L, "Hollow Knight", 4.5, List.of("Action", "Platformer"), List.of("PC", "Nintendo Switch"));

        sofia = usuario("Sofia", hades, 5, celeste, 2);
        ana = usuario("Ana", hades, 5, celeste, 2, hollowKnight, 5);
        bruno = usuario("Bruno", hades, 1, celeste, 5, fifa, 5);
        novato = usuario("Novato");
    }

    @Test
    void generosEPlataformasSaoNosCompartilhados() {
        Long generosAction = neo4jClient.query("MATCH (g:Genero {nome: 'Action'}) RETURN count(g)")
                .fetchAs(Long.class).one().orElseThrow();
        Long plataformasPc = neo4jClient.query("MATCH (p:Plataforma {nome: 'PC'}) RETURN count(p)")
                .fetchAs(Long.class).one().orElseThrow();

        assertThat(generosAction).isEqualTo(1);
        assertThat(plataformasPc).isEqualTo(1);
    }

    @Test
    void porPerfilRecomendaJogosParecidosComOsQueOUsuarioCurtiu() {
        List<JogoRecomendado> recomendacoes = repository.recomendarPorPerfil(sofia, 4, 10);

        assertThat(recomendacoes).extracting(JogoRecomendado::nome)
                .containsExactly("Dead Cells", "Hollow Knight");
        assertThat(recomendacoes.getFirst().motivo()).isEqualTo("Porque você curte Action e Roguelike");
    }

    @Test
    void porPerfilDeUsuarioSemAvaliacoesRetornaVazio() {
        assertThat(repository.recomendarPorPerfil(novato, 4, 10)).isEmpty();
    }

    @Test
    void porJogadoresParecidosUsaApenasQuemTemGostoParecido() {
        List<JogoRecomendado> recomendacoes = repository.recomendarPorJogadoresParecidos(sofia, 4, 10);

        assertThat(recomendacoes).extracting(JogoRecomendado::nome).containsExactly("Hollow Knight");
        assertThat(recomendacoes.getFirst().motivo())
                .isEqualTo("Bem avaliado por 1 jogador com gosto parecido com o seu");
    }

    @Test
    void popularesIgnoraJogosJaAvaliadosEPesaANotaDaRawg() {
        List<JogoRecomendado> populares = repository.buscarPopulares(sofia, 10);

        assertThat(populares).extracting(JogoRecomendado::nome)
                .containsExactly("Hollow Knight", "Dead Cells", "FIFA");
    }

    @Test
    void similaresPriorizaGenerosSobrePlataformas() {
        List<JogoRecomendado> similares = repository.buscarSimilares(1L, 10);

        assertThat(similares).extracting(JogoRecomendado::nome)
                .containsExactly("Dead Cells", "Hollow Knight", "Celeste");
        assertThat(similares.getLast().motivo()).isEqualTo("Disponível nas mesmas plataformas");
    }

    @Test
    void usuariosCompativeisOrdenaPelaConcordanciaDasNotas() {
        List<UsuarioCompativel> compativeis = repository.buscarUsuariosCompativeis(sofia, 10);

        assertThat(compativeis).extracting(UsuarioCompativel::usuarioId).containsExactly(ana, bruno);
        assertThat(compativeis.getFirst().compatibilidade()).isEqualTo(100);
        assertThat(compativeis.getFirst().jogosEmComum()).isEqualTo(2);
        assertThat(compativeis.getLast().compatibilidade()).isEqualTo(13);
    }

    @Test
    void limiteEhRespeitado() {
        assertThat(repository.buscarSimilares(1L, 1)).hasSize(1);
        assertThat(repository.buscarUsuariosCompativeis(sofia, 1)).hasSize(1);
    }

    @Test
    void favoritoContaComoGostarEJogosSalvosNaoSaoRecomendados() {
        Usuario usuario = usuarioRepository.findById(novato).orElseThrow();
        usuario.favoritar(jogoRepository.findById(3L).orElseThrow());
        usuario.adicionarDesejo(jogoRepository.findById(5L).orElseThrow());
        usuarioRepository.save(usuario);

        List<JogoRecomendado> recomendacoes = repository.recomendarPorPerfil(novato, 4, 10);

        assertThat(recomendacoes).extracting(JogoRecomendado::nome)
                .containsExactly("Hades", "Dead Cells")
                .doesNotContain("Celeste", "Hollow Knight");
        assertThat(repository.buscarPopulares(novato, 10)).extracting(JogoRecomendado::nome)
                .doesNotContain("Celeste", "Hollow Knight");
    }

    private Jogo salvar(Long id, String nome, Double notaRawg, List<String> generos, List<String> plataformas) {
        return jogoRepository.save(Jogo.importar(
                id, nome, null, LocalDate.of(2020, 1, 1), null, notaRawg, generos, plataformas
        ));
    }

    private UUID usuario(String nome, Object... avaliacoes) {
        Usuario usuario = Usuario.builder()
                .nome(nome)
                .email(nome.toLowerCase() + "@playmatch.com")
                .senhaHash("hash")
                .build();

        for (int i = 0; i < avaliacoes.length; i += 2) {
            usuario.avaliar((Jogo) avaliacoes[i], (Integer) avaliacoes[i + 1], null);
        }

        return usuarioRepository.save(usuario).getId();
    }
}
