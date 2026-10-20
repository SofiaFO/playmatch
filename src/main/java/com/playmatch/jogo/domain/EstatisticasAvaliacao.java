package com.playmatch.jogo.domain;


/**
 * The Domain Record EstatisticasAvaliacao
 *
 * @param media média das notas dadas pelos usuários do PlayMatch, ou {@code null} sem avaliações
 * @param total quantidade de avaliações
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
public record EstatisticasAvaliacao(
        Double media,
        long total
) {

    public static EstatisticasAvaliacao vazia() {
        return new EstatisticasAvaliacao(null, 0);
    }

    public static EstatisticasAvaliacao de(Double media, long total) {
        if (total == 0 || media == null) {
            return vazia();
        }

        return new EstatisticasAvaliacao(Math.round(media * 10) / 10.0, total);
    }
}
