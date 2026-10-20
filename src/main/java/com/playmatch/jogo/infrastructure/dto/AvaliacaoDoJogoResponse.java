package com.playmatch.jogo.infrastructure.dto;


import com.playmatch.jogo.domain.AvaliacaoDoJogo;

import java.time.Instant;
import java.util.UUID;

/**
 * The Record AvaliacaoDoJogoResponse
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
public record AvaliacaoDoJogoResponse(
        UUID usuarioId,
        String nomeUsuario,
        Integer nota,
        String resenha,
        Instant criadaEm,
        Instant atualizadaEm
) {

    public static AvaliacaoDoJogoResponse fromDomain(AvaliacaoDoJogo avaliacao) {
        return new AvaliacaoDoJogoResponse(
                avaliacao.usuarioId(),
                avaliacao.nomeUsuario(),
                avaliacao.nota(),
                avaliacao.resenha(),
                avaliacao.criadaEm(),
                avaliacao.atualizadaEm()
        );
    }
}
