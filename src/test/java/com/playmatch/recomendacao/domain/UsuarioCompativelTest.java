package com.playmatch.recomendacao.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class UsuarioCompativelTest {

    @ParameterizedTest
    @CsvSource({
            "0.0, 100",
            "1.0, 75",
            "2.0, 50",
            "4.0, 0"
    })
    void calculaCompatibilidadePelaDiferencaMediaDasNotas(double diferencaMedia, int esperado) {
        assertThat(UsuarioCompativel.calcularCompatibilidade(diferencaMedia)).isEqualTo(esperado);
    }

    @Test
    void diferencaForaDaEscalaEhLimitada() {
        assertThat(UsuarioCompativel.calcularCompatibilidade(-1)).isEqualTo(100);
        assertThat(UsuarioCompativel.calcularCompatibilidade(9)).isZero();
    }
}
