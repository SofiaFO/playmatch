package com.playmatch.jogo.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstatisticasAvaliacaoTest {

    @Test
    void arredondaAMediaParaUmaCasaDecimal() {
        assertThat(EstatisticasAvaliacao.de(4.666, 3)).isEqualTo(new EstatisticasAvaliacao(4.7, 3));
    }

    @Test
    void semAvaliacoesAMediaFicaNula() {
        assertThat(EstatisticasAvaliacao.de(null, 0)).isEqualTo(EstatisticasAvaliacao.vazia());
        assertThat(EstatisticasAvaliacao.vazia().media()).isNull();
    }
}
