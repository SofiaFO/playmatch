package com.playmatch.recomendacao.infrastructure.controller;

import com.playmatch.recomendacao.application.BuscarJogosSimilaresService;
import com.playmatch.recomendacao.application.BuscarUsuariosCompativeisService;
import com.playmatch.recomendacao.application.RecomendarJogosService;
import com.playmatch.recomendacao.application.RecomendarPorJogadoresParecidosService;
import com.playmatch.recomendacao.domain.JogoRecomendado;
import com.playmatch.shared.infrastructure.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RecomendacaoControllerTest {

    private final RecomendarJogosService recomendarJogosService = mock(RecomendarJogosService.class);

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        RecomendacaoController controller = new RecomendacaoController(
                recomendarJogosService,
                mock(RecomendarPorJogadoresParecidosService.class),
                mock(BuscarJogosSimilaresService.class),
                mock(BuscarUsuariosCompativeisService.class)
        );

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void usaLimitePadraoEArredondaAPontuacao() throws Exception {
        when(recomendarJogosService.executar(10)).thenReturn(List.of(
                JogoRecomendado.porGeneros(1L, "Hades", null, 4.6, 10.0 / 3, List.of("Action"))
        ));

        mockMvc.perform(get("/recomendacoes/jogos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Hades"))
                .andExpect(jsonPath("$[0].pontuacao").value(3.33))
                .andExpect(jsonPath("$[0].motivo").value("Porque você curte Action"));
    }

    @Test
    void limiteForaDoIntervaloRetorna400() throws Exception {
        mockMvc.perform(get("/recomendacoes/jogos").param("limite", "51"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("O limite deve ser no máximo 50"));
    }
}
