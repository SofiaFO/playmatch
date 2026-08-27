package com.playmatch.jogo.infrastructure.dto;


import com.playmatch.jogo.domain.Genero;
import com.playmatch.jogo.domain.Jogo;
import lombok.Builder;

import java.util.List;

/**
 * The Record JogoResponse
 *
 * @author Sofia Ferreira de Oliveira
 * @since 26/08/2026
 */
@Builder
public record JogoResponse(

        Long id,

        String nome,

        Integer ano,

        List<String> generos
) {

    public static JogoResponse fromDomain(Jogo jogo) {
        return JogoResponse.builder()
                .id(jogo.getId())
                .nome(jogo.getNome())
                .ano(jogo.getAno())
                .generos(
                        jogo.getGeneros().stream()
                                .map(Genero::getNome)
                                .sorted()
                                .toList()
                )
                .build();
    }
}
