package com.playmatch.recomendacao.application;

import com.playmatch.recomendacao.domain.JogoRecomendado;
import com.playmatch.recomendacao.infrastructure.repository.RecomendacaoRepository;
import com.playmatch.shared.application.security.UsuarioAutenticado;
import com.playmatch.shared.exception.BusinessException;
import com.playmatch.usuario.infrastructure.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecomendarJogosServiceTest {

    @Mock
    private RecomendacaoRepository recomendacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioAutenticado usuarioAutenticado;

    @InjectMocks
    private RecomendarJogosService service;

    private final UUID usuarioId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        when(usuarioAutenticado.id()).thenReturn(usuarioId);
    }

    private static JogoRecomendado jogo(long id) {
        return JogoRecomendado.popular(id, "Jogo " + id, null, null, 1.0);
    }

    @Test
    void perfilCompletoNaoConsultaPopulares() {
        when(usuarioRepository.existsById(usuarioId)).thenReturn(true);
        when(recomendacaoRepository.recomendarPorPerfil(usuarioId, 4, 2)).thenReturn(List.of(jogo(1), jogo(2)));

        assertThat(service.executar(2)).extracting(JogoRecomendado::jogoId).containsExactly(1L, 2L);
        verify(recomendacaoRepository, never()).buscarPopulares(any(), anyInt());
    }

    @Test
    void perfilIncompletoEhCompletadoComPopularesSemRepetir() {
        when(usuarioRepository.existsById(usuarioId)).thenReturn(true);
        when(recomendacaoRepository.recomendarPorPerfil(usuarioId, 4, 3)).thenReturn(List.of(jogo(1)));
        when(recomendacaoRepository.buscarPopulares(usuarioId, 3)).thenReturn(List.of(jogo(1), jogo(7), jogo(8)));

        assertThat(service.executar(3)).extracting(JogoRecomendado::jogoId).containsExactly(1L, 7L, 8L);
    }

    @Test
    void usuarioNovoRecebeSoPopulares() {
        when(usuarioRepository.existsById(usuarioId)).thenReturn(true);
        when(recomendacaoRepository.recomendarPorPerfil(usuarioId, 4, 2)).thenReturn(List.of());
        when(recomendacaoRepository.buscarPopulares(usuarioId, 2)).thenReturn(List.of(jogo(5), jogo(6)));

        assertThat(service.executar(2)).extracting(JogoRecomendado::jogoId).containsExactly(5L, 6L);
    }

    @Test
    void usuarioDoTokenQueNaoExisteMaisRetorna404() {
        when(usuarioRepository.existsById(usuarioId)).thenReturn(false);

        assertThatThrownBy(() -> service.executar(5))
                .isInstanceOf(BusinessException.class)
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.NOT_FOUND);
    }
}
