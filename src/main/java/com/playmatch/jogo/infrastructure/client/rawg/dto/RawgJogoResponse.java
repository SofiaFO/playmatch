package com.playmatch.jogo.infrastructure.client.rawg.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

/**
 * Record {NAME}
 *
 * @author Sofia Ferreira de Oliveira
 * @since 27/08/2026
 */
public record RawgJogoResponse(
        Long id,

        String name,

        LocalDate released,

        @JsonProperty("background_image")
        String background_image,

        Double rating,

        List<RawGeneroResponse> genres,

        List<RawPlataformaResponse> platforms
) {
}
