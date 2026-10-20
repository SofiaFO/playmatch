package com.playmatch.usuario.application.lista;

import com.playmatch.jogo.application.ImportarJogoService;
import com.playmatch.jogo.domain.Jogo;
import com.playmatch.shared.application.security.UsuarioAutenticado;
import com.playmatch.shared.exception.BusinessException;
import com.playmatch.usuario.domain.TipoLista;
import com.playmatch.usuario.domain.Usuario;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdicionarJogoNaListaServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ImportarJogoService importarJogoService;

    @Mock
    private UsuarioAutenticado usuarioAutenticado;

    @InjectMocks
    private AdicionarJogoNaListaService service;

    private final UUID usuarioId = UUID.randomUUID();

    private Usuario usuario() {
        Usuario usuario = Usuario.builder().nome("Ana").email("ana@email.com").build();
        ReflectionTestUtils.setField(usuario, "id", usuarioId);
        return usuario;
    }

    @Test
    void naoPermiteMexerNaListaDeOutroUsuario() {
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario()));
        when(usuarioAutenticado.id()).thenReturn(UUID.randomUUID());

        assertThatThrownBy(() -> service.executar(usuarioId, TipoLista.FAVORITOS, 1L))
                .isInstanceOf(BusinessException.class)
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.FORBIDDEN);

        verifyNoInteractions(importarJogoService);
    }

    @Test
    void importaOJogoSeNecessarioEAdicionaNaLista() {
        Usuario usuario = usuario();
        Jogo jogo = Jogo.importar(1L, "Hades", null, null, null, null, null, null);
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioAutenticado.id()).thenReturn(usuarioId);
        when(importarJogoService.obterOuImportar(1L)).thenReturn(jogo);

        service.executar(usuarioId, TipoLista.DESEJOS, 1L);

        assertThat(usuario.getDesejos()).hasSize(1);
        verify(usuarioRepository).save(usuario);
    }
}
