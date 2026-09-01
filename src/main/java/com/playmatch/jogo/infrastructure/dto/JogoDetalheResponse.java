package com.playmatch.jogo.infrastructure.dto;


import java.time.LocalDate;
import java.util.List;

/**
 * The Domain Class JogoDetalheResponse
 *
 * @author Sofia Ferreira de Oliveira
 * @since 01/09/2026
 */

public record JogoDetalheResponse (
        Long idExterno,

        String nome,

        String descricao,

        LocalDate lancamento,

        String imagemUrl,

        Double avaliacao,

        List<String> generos,

        List<String> plataformas
) {
}
