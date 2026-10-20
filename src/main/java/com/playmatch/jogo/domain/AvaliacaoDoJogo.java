package com.playmatch.jogo.domain;


import java.time.Instant;
import java.util.UUID;

/**
 * The Domain Record AvaliacaoDoJogo
 *
 * Visão de uma avaliação a partir do jogo: quem avaliou, a nota e a resenha.
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
public record AvaliacaoDoJogo(
        UUID usuarioId,
        String nomeUsuario,
        Integer nota,
        String resenha,
        Instant criadaEm,
        Instant atualizadaEm
) {
}
