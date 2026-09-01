package com.playmatch.usuario.infrastructure.dto;


/**
 * The Record AvaliacaoResponse
 *
 * @author Sofia Ferreira de Oliveira
 * @since 01/09/2026
 */
public record AvaliacaoResponse(
        Long jogoId,
        String nomeJogo,
        Integer nota
) {
}
