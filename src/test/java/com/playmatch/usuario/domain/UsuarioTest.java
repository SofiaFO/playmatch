package com.playmatch.usuario.domain;

import com.playmatch.jogo.domain.Jogo;
import com.playmatch.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsuarioTest {

    private Usuario novoUsuario(UUID id) {
        Usuario usuario = Usuario.builder().nome("Sofia").email("sofia@email.com").build();
        ReflectionTestUtils.setField(usuario, "id", id);
        return usuario;
    }

    private Jogo novoJogo(Long id) {
        Jogo jogo = new Jogo();
        ReflectionTestUtils.setField(jogo, "id", id);
        return jogo;
    }

    @Test
    void validarPermissaoAceitaOProprioUsuario() {
        UUID id = UUID.randomUUID();

        novoUsuario(id).validarPermissao(id);
    }

    @Test
    void validarPermissaoRecusaOutroUsuario() {
        Usuario usuario = novoUsuario(UUID.randomUUID());

        assertThatThrownBy(() -> usuario.validarPermissao(UUID.randomUUID()))
                .isInstanceOf(BusinessException.class)
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void avaliarMesmoJogoDuasVezesAtualizaANota() {
        Usuario usuario = novoUsuario(UUID.randomUUID());
        Jogo jogo = novoJogo(10L);

        usuario.avaliar(jogo, 3, null);
        usuario.avaliar(jogo, 5, "Melhorou muito");

        assertThat(usuario.getAvaliacoes()).hasSize(1);
        assertThat(usuario.buscarAvaliacao(10L)).get().extracting(Avaliacao::getNota).isEqualTo(5);
    }

    @Test
    void deletarAvaliacaoInexistenteLancaNotFound() {
        Usuario usuario = novoUsuario(UUID.randomUUID());

        assertThatThrownBy(() -> usuario.deletarAvaliacao(99L))
                .isInstanceOf(BusinessException.class)
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void avaliarGuardaAResenhaSemEspacosExtras() {
        Usuario usuario = novoUsuario(UUID.randomUUID());

        usuario.avaliar(novoJogo(1L), 4, "  Ótimo jogo  ");

        Avaliacao avaliacao = usuario.buscarAvaliacao(1L).orElseThrow();
        assertThat(avaliacao.getResenha()).isEqualTo("Ótimo jogo");
        assertThat(avaliacao.getCriadaEm()).isNotNull();
    }

    @Test
    void resenhaEmBrancoViraNula() {
        Usuario usuario = novoUsuario(UUID.randomUUID());

        usuario.avaliar(novoJogo(1L), 4, "   ");

        assertThat(usuario.buscarAvaliacao(1L).orElseThrow().getResenha()).isNull();
    }

    @Test
    void resenhaMuitoLongaEhRecusada() {
        Usuario usuario = novoUsuario(UUID.randomUUID());
        String resenha = "a".repeat(Avaliacao.TAMANHO_MAXIMO_RESENHA + 1);

        assertThatThrownBy(() -> usuario.avaliar(novoJogo(1L), 4, resenha))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void avaliarRemoveOJogoDaListaDeDesejos() {
        Usuario usuario = novoUsuario(UUID.randomUUID());
        Jogo jogo = novoJogo(1L);
        usuario.adicionarDesejo(jogo);

        usuario.avaliar(jogo, 5, null);

        assertThat(usuario.getDesejos()).isEmpty();
    }

    @Test
    void naoDaParaDesejarUmJogoJaAvaliado() {
        Usuario usuario = novoUsuario(UUID.randomUUID());
        Jogo jogo = novoJogo(1L);
        usuario.avaliar(jogo, 5, null);

        assertThatThrownBy(() -> usuario.adicionarDesejo(jogo))
                .isInstanceOf(BusinessException.class)
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT);
    }

    @Test
    void favoritarDuasVezesRetornaConflito() {
        Usuario usuario = novoUsuario(UUID.randomUUID());
        Jogo jogo = novoJogo(1L);
        usuario.adicionarNaLista(TipoLista.FAVORITOS, jogo);

        assertThatThrownBy(() -> usuario.adicionarNaLista(TipoLista.FAVORITOS, jogo))
                .isInstanceOf(BusinessException.class)
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void jogoPodeEstarNosFavoritosENaListaDeDesejosAoMesmoTempo() {
        Usuario usuario = novoUsuario(UUID.randomUUID());
        Jogo jogo = novoJogo(1L);

        usuario.adicionarNaLista(TipoLista.FAVORITOS, jogo);
        usuario.adicionarNaLista(TipoLista.DESEJOS, jogo);

        assertThat(usuario.getLista(TipoLista.FAVORITOS)).hasSize(1);
        assertThat(usuario.getLista(TipoLista.DESEJOS)).hasSize(1);
    }

    @Test
    void removerJogoQueNaoEstaNaListaRetornaNotFound() {
        Usuario usuario = novoUsuario(UUID.randomUUID());

        assertThatThrownBy(() -> usuario.removerDaLista(TipoLista.DESEJOS, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }
}
