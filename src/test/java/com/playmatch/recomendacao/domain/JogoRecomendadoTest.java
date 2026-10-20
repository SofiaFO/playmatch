package com.playmatch.recomendacao.domain;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JogoRecomendadoTest {

    @Test
    void motivoPorGenerosJuntaOsNomesEmOrdemAlfabetica() {
        JogoRecomendado jogo = JogoRecomendado.porGeneros(1L, "Hades", null, 4.6, 10, List.of("RPG", "Action", "Indie"));

        assertThat(jogo.motivo()).isEqualTo("Porque você curte Action, Indie e RPG");
    }

    @Test
    void motivoPorGenerosComUmGenero() {
        JogoRecomendado jogo = JogoRecomendado.porGeneros(1L, "Hades", null, 4.6, 10, Arrays.asList("Action", null));

        assertThat(jogo.motivo()).isEqualTo("Porque você curte Action");
    }

    @Test
    void motivoPorGenerosSemGeneroEmComumCitaAsPlataformas() {
        JogoRecomendado jogo = JogoRecomendado.porGeneros(1L, "Hades", null, 4.6, 2, List.of());

        assertThat(jogo.motivo()).isEqualTo("Disponível nas plataformas dos jogos que você curte");
    }

    @Test
    void motivoPorJogadoresParecidosUsaSingularEPlural() {
        assertThat(JogoRecomendado.porJogadoresParecidos(1L, "Hades", null, null, 5, 1).motivo())
                .isEqualTo("Bem avaliado por 1 jogador com gosto parecido com o seu");
        assertThat(JogoRecomendado.porJogadoresParecidos(1L, "Hades", null, null, 5, 3).motivo())
                .isEqualTo("Bem avaliado por 3 jogadores com gosto parecido com o seu");
    }
}
