package com.playmatch.jogo.infrastructure.client.rawg.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

/**
 * The DTO RawgJogoResponse
 *
 * @author Sofia Ferreira de Oliveira
 * @since 27/08/2026
 */
public record RawgJogoResponse(
        Long id,

        String name,

        LocalDate released,

        @JsonProperty("background_image")
        String backgroundImage,

        Double rating,

        List<RawGeneroResponse> genres,

        List<RawJogoPlataformaResponse> platforms
) {

    public RawgJogoResponse {
        genres = genres == null ? List.of() : genres;
        platforms = platforms == null ? List.of() : platforms;
    }
}
