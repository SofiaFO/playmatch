package com.playmatch.jogo.infrastructure.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.playmatch.jogo.infrastructure.client.rawg.dto.RawGeneroResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * The DTO RawgJogoDetalhesResponse
 *
 * @author Sofia Ferreira de Oliveira
 * @since 01/09/2026
 */
public record RawgJogoDetalhesResponse(

        Long id,

        String name,

        @JsonProperty("description_raw")
        String description,

        LocalDate released,

        @JsonProperty("background_image")
        String backgroundImage,

        Double rating,

        List<RawGeneroResponse> genres,

        List<RawJogoPlataformaResponse> platforms

) {
}
