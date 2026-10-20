package com.playmatch.usuario.application.perfil;

import com.playmatch.jogo.domain.Jogo;
import com.playmatch.shared.application.security.UsuarioAutenticado;
import com.playmatch.usuario.domain.Avaliacao;
import com.playmatch.usuario.domain.EstatisticasPerfil;
import com.playmatch.usuario.domain.Usuario;
import com.playmatch.usuario.infrastructure.dto.avaliacao.AvaliacaoResponse;
import com.playmatch.usuario.infrastructure.dto.perfil.PerfilResponse;
import com.playmatch.usuario.infrastructure.repository.PerfilRepository;
import com.playmatch.usuario.infrastructure.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarPerfilServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PerfilRepository perfilRepository;

    @Mock
    private UsuarioAutenticado usuarioAutenticado;

    @InjectMocks
    private BuscarPerfilService service;

    private final UUID donoId = UUID.randomUUID();

    private static final EstatisticasPerfil ESTATISTICAS = new EstatisticasPerfil(
            7, 4.2, 1, 2, 3, 4, List.of("Action"), List.of("PC"), true
    );

    private Usuario donoComAvaliacoes(int quantidade) {
        Usuario usuario = Usuario.builder().nome("Ana").email("ana@email.com").build();
        ReflectionTestUtils.setField(usuario, "id", donoId);

        for (int i = 1; i <= quantidade; i++) {
            Jogo jogo = Jogo.importar((long) i, "Jogo " + i, null, null, null, null, null, null);
            usuario.avaliar(jogo, 4, null);
            Avaliacao avaliacao = usuario.buscarAvaliacao((long) i).orElseThrow();
            ReflectionTestUtils.setField(avaliacao, "atualizadaEm", Instant.parse("2026-10-01T00:00:00Z").plusSeconds(i));
        }

        return usuario;
    }

    @Test
    void perfilDeOutroUsuarioInformaSeVoceSegue() {
        when(usuarioRepository.findById(donoId)).thenReturn(Optional.of(donoComAvaliacoes(0)));
        UUID visitante = UUID.randomUUID();
        when(usuarioAutenticado.id()).thenReturn(visitante);
        when(perfilRepository.buscarEstatisticas(donoId, visitante)).thenReturn(ESTATISTICAS);

        PerfilResponse perfil = service.executar(donoId);

        assertThat(perfil.ehVoce()).isFalse();
        assertThat(perfil.voceSegue()).isTrue();
        assertThat(perfil.seguidores()).isEqualTo(3);
        assertThat(perfil.generosFavoritos()).containsExactly("Action");
    }

    @Test
    void proprioPerfilNaoTemVoceSegueETrazAsCincoAvaliacoesMaisRecentes() {
        when(usuarioRepository.findById(donoId)).thenReturn(Optional.of(donoComAvaliacoes(7)));
        when(usuarioAutenticado.id()).thenReturn(donoId);
        when(perfilRepository.buscarEstatisticas(donoId, donoId)).thenReturn(ESTATISTICAS);

        PerfilResponse perfil = service.executarParaUsuarioAutenticado();

        assertThat(perfil.ehVoce()).isTrue();
        assertThat(perfil.voceSegue()).isNull();
        assertThat(perfil.ultimasAvaliacoes()).extracting(AvaliacaoResponse::jogoId)
                .containsExactly(7L, 6L, 5L, 4L, 3L);
    }
}
