package com.playmatch.jogo.infrastructure.dto;


import com.playmatch.jogo.infrastructure.client.rawg.dto.RawPlataformaResponse;


/**
 * The RAWG DTO RawJogoPlataformaResponse
 *
 * @author Sofia Ferreira de Oliveira
 * @since 28/08/2026
 */
public record RawJogoPlataformaResponse(
        RawPlataformaResponse platform
) {
}