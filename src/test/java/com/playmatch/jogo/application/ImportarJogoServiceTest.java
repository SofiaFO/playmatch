package com.playmatch.jogo.application;

import com.playmatch.jogo.domain.Genero;
import com.playmatch.jogo.domain.Jogo;
import com.playmatch.jogo.domain.Plataforma;
import com.playmatch.jogo.infrastructure.client.rawg.RawgClient;
import com.playmatch.jogo.infrastructure.client.rawg.dto.RawGeneroResponse;
import com.playmatch.jogo.infrastructure.client.rawg.dto.RawJogoPlataformaResponse;
import com.playmatch.jogo.infrastructure.client.rawg.dto.RawPlataformaResponse;
import com.playmatch.jogo.infrastructure.client.rawg.dto.RawgJogoDetalhesResponse;
import com.playmatch.jogo.infrastructure.repository.JogoRepository;
import com.playmatch.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImportarJogoServiceTest {

    @Mock
    private JogoRepository jogoRepository;

    @Mock
    private RawgClient rawgClient;

    @InjectMocks
    private ImportarJogoService service;

    @Test
    void jogoJaImportadoNaoConsultaARawg() {
        Jogo existente = Jogo.importar(10L, "Hades", null, null, null, null, null, null);
        when(jogoRepository.findById(10L)).thenReturn(Optional.of(existente));

        ResultadoImportacao resultado = service.executar(10L);

        assertThat(resultado.criado()).isFalse();
        assertThat(resultado.jogo()).isSameAs(existente);
        verifyNoInteractions(rawgClient);
        verify(jogoRepository, never()).save(any());
    }

    @Test
    void jogoNovoEhBuscadoNaRawgESalvo() {
        RawgJogoDetalhesResponse rawg = new RawgJogoDetalhesResponse(
                10L,
                "Hades",
                "Roguelike",
                LocalDate.of(2020, 9, 17),
                "https://img/hades.jpg",
                4.6,
                List.of(new RawGeneroResponse(1L, "Action")),
                List.of(new RawJogoPlataformaResponse(new RawPlataformaResponse(4L, "PC")))
        );

        when(jogoRepository.findById(10L)).thenReturn(Optional.empty());
        when(rawgClient.buscarPorId(10L)).thenReturn(rawg);
        when(jogoRepository.save(any(Jogo.class))).thenAnswer(invocacao -> invocacao.getArgument(0));

        ResultadoImportacao resultado = service.executar(10L);

        assertThat(resultado.criado()).isTrue();
        Jogo jogo = resultado.jogo();
        assertThat(jogo.getId()).isEqualTo(10L);
        assertThat(jogo.getAno()).isEqualTo(2020);
        assertThat(jogo.getNotaRawg()).isEqualTo(4.6);
        assertThat(jogo.getGeneros()).extracting(Genero::getNome).containsExactly("Action");
        assertThat(jogo.getPlataformas()).extracting(Plataforma::getNome).containsExactly("PC");
    }

    @Test
    void jogoInexistenteNaRawgPropagaNotFound() {
        when(jogoRepository.findById(99L)).thenReturn(Optional.empty());
        when(rawgClient.buscarPorId(99L))
                .thenThrow(new BusinessException("Jogo não encontrado no catálogo externo", HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> service.obterOuImportar(99L))
                .isInstanceOf(BusinessException.class)
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(jogoRepository, never()).save(any());
    }
}
