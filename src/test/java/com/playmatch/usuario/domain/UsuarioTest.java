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

        usuario.avaliar(jogo, 3);
        usuario.avaliar(jogo, 5);

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
}
