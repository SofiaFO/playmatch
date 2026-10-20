package com.playmatch.usuario.application.avaliacao;

import com.playmatch.jogo.application.ImportarJogoService;
import com.playmatch.jogo.domain.Jogo;
import com.playmatch.shared.application.security.UsuarioAutenticado;
import com.playmatch.shared.exception.BusinessException;
import com.playmatch.usuario.domain.Usuario;
import com.playmatch.usuario.infrastructure.dto.avaliacao.AvaliarJogoRequest;
import com.playmatch.usuario.infrastructure.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AvaliarJogoServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ImportarJogoService importarJogoService;

    @Mock
    private UsuarioAutenticado usuarioAutenticado;

    @InjectMocks
    private AvaliarJogoService service;

    private final UUID usuarioId = UUID.randomUUID();

    private Usuario usuario() {
        Usuario usuario = Usuario.builder().nome("Sofia").email("sofia@email.com").build();
        ReflectionTestUtils.setField(usuario, "id", usuarioId);
        return usuario;
    }

    @Test
    void naoPermiteAvaliarEmNomeDeOutroUsuario() {
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario()));
        when(usuarioAutenticado.id()).thenReturn(UUID.randomUUID());

        assertThatThrownBy(() -> service.executar(new AvaliarJogoRequest(usuarioId, 1L, 5, null)))
                .isInstanceOf(BusinessException.class)
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.FORBIDDEN);

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void avaliaQuandoOUsuarioEhODono() {
        Usuario usuario = usuario();
        Jogo jogo = new Jogo();
        ReflectionTestUtils.setField(jogo, "id", 1L);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioAutenticado.id()).thenReturn(usuarioId);
        when(importarJogoService.obterOuImportar(1L)).thenReturn(jogo);

        service.executar(new AvaliarJogoRequest(usuarioId, 1L, 4, "Muito bom"));

        verify(usuarioRepository).save(usuario);
        assertThat(usuario.buscarAvaliacao(1L)).isPresent();
    }
}
