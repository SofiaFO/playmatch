package com.playmatch.jogo.infrastructure.client.rawg.dto;


import java.util.List;

/**
 * The DTO RawgPesquisaResponse
 *
 * @author Sofia Ferreira de Oliveira
 * @since 27/08/2026
 */
public record RawgPesquisaResponse(
        Integer count,
        List<RawgJogoResponse> results
) {
}
