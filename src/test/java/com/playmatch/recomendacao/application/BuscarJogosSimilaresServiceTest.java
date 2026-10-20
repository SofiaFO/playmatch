package com.playmatch.recomendacao.application;

import com.playmatch.jogo.infrastructure.repository.JogoRepository;
import com.playmatch.recomendacao.infrastructure.repository.RecomendacaoRepository;
import com.playmatch.shared.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarJogosSimilaresServiceTest {

    @Mock
    private RecomendacaoRepository recomendacaoRepository;

    @Mock
    private JogoRepository jogoRepository;

    @InjectMocks
    private BuscarJogosSimilaresService service;

    @Test
    void jogoForaDoCatalogoRetorna404() {
        when(jogoRepository.existsById(42L)).thenReturn(false);

        assertThatThrownBy(() -> service.executar(42L, 10))
                .isInstanceOf(BusinessException.class)
                .extracting("httpStatus")
                .isEqualTo(HttpStatus.NOT_FOUND);

        verifyNoInteractions(recomendacaoRepository);
    }
}
