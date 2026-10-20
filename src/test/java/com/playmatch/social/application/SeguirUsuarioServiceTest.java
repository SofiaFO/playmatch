package com.playmatch.social.application;

import com.playmatch.shared.application.security.UsuarioAutenticado;
import com.playmatch.shared.exception.BusinessException;
import com.playmatch.social.infrastructure.repository.SeguidorRepository;
import com.playmatch.usuario.infrastructure.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeguirUsuarioServiceTest {

    @Mock
    private SeguidorRepository seguidorRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioAutenticado usuarioAutenticado;

    @InjectMocks
    private SeguirUsuarioService service;

    private final UUID eu = UUID.randomUUID();
    private final UUID outro = UUID.randomUUID();

    @Test
    void naoPermiteSeguirASiMesmo() {
        when(usuarioAutenticado.id()).thenReturn(eu);

        assertThatThrownBy(() -> service.executar(eu))
                .isInstanceOf(BusinessException.class)
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT);

        verifyNoInteractions(seguidorRepository);
    }

    @Test
    void usuarioInexistenteRetorna404() {
        when(usuarioAutenticado.id()).thenReturn(eu);
        when(usuarioRepository.existsById(outro)).thenReturn(false);

        assertThatThrownBy(() -> service.executar(outro))
                .isInstanceOf(BusinessException.class)
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.NOT_FOUND);

        verifyNoInteractions(seguidorRepository);
    }

    @Test
    void segueEmNomeDoUsuarioAutenticado() {
        when(usuarioAutenticado.id()).thenReturn(eu);
        when(usuarioRepository.existsById(outro)).thenReturn(true);

        service.executar(outro);

        verify(seguidorRepository).seguir(eu, outro);
    }
}
