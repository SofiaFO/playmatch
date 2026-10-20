package com.playmatch.jogo.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JogoTest {

    @Test
    void importarPreencheAnoEDescartaNomesInvalidos() {
        Jogo jogo = Jogo.importar(
                3498L,
                "  Grand Theft Auto V ",
                "Descrição",
                LocalDate.of(2013, 9, 17),
                "https://img/gta.jpg",
                4.47,
                Arrays.asList("Action", null, " ", "Action"),
                List.of("PC", "PlayStation 5")
        );

        assertThat(jogo.getNome()).isEqualTo("Grand Theft Auto V");
        assertThat(jogo.getAno()).isEqualTo(2013);
        assertThat(jogo.getGeneros()).extracting(Genero::getNome).containsExactly("Action");
        assertThat(jogo.getPlataformas()).extracting(Plataforma::getNome)
                .containsExactlyInAnyOrder("PC", "PlayStation 5");
    }

    @Test
    void importarSemLancamentoDeixaAnoNulo() {
        Jogo jogo = Jogo.importar(1L, "Jogo", null, null, null, null, null, null);

        assertThat(jogo.getAno()).isNull();
        assertThat(jogo.getGeneros()).isEmpty();
        assertThat(jogo.getPlataformas()).isEmpty();
    }

    @Test
    void importarSemNomeLancaErro() {
        assertThatThrownBy(() -> Jogo.importar(1L, " ", null, null, null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O nome do jogo é obrigatório.");
    }
}
