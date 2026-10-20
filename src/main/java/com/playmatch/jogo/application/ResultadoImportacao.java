package com.playmatch.jogo.application;


import com.playmatch.jogo.domain.Jogo;

/**
 * The Record ResultadoImportacao
 *
 * @param jogo   jogo salvo no catálogo local
 * @param criado {@code true} quando o jogo foi trazido da RAWG nesta chamada
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
public record ResultadoImportacao(
        Jogo jogo,
        boolean criado
) {
}
