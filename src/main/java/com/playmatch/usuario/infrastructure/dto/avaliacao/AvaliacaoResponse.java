package com.playmatch.usuario.infrastructure.dto.avaliacao;


import com.playmatch.usuario.domain.Avaliacao;

import java.time.Instant;

/**
 * The Record AvaliacaoResponse
 *
 * @author Sofia Ferreira de Oliveira
 * @since 01/09/2026
 */
public record AvaliacaoResponse(
        Long jogoId,
        String nomeJogo,
        Integer nota,
        String resenha,
        Instant criadaEm,
        Instant atualizadaEm
) {

    public static AvaliacaoResponse fromDomain(Avaliacao avaliacao) {
        return new AvaliacaoResponse(
                avaliacao.getJogo().getId(),
                avaliacao.getJogo().getNome(),
                avaliacao.getNota(),
                avaliacao.getResenha(),
                avaliacao.getCriadaEm(),
                avaliacao.getAtualizadaEm()
        );
    }
}
