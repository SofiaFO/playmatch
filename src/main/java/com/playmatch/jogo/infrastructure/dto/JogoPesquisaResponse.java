package com.playmatch.jogo.infrastructure.dto;


import java.time.LocalDate;
import java.util.List;

/**
 * The DTO JogoPesquisaResponse
 *
 * @author Sofia Ferreira de Oliveira
 * @since 27/08/2026
 */
public record JogoPesquisaResponse(
        Long idExterno,

        String nome,

        LocalDate lancamento,

        String imagemUrl,

        Double avaliacao,

        List<String> generos,

        List<String> plataformas
) {
}
