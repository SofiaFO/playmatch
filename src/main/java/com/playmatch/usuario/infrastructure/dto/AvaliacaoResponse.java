package com.playmatch.usuario.infrastructure.dto;


import com.playmatch.usuario.domain.Avaliacao;

/**
 * The Record AvaliacaoResponse
 *
 * @author Sofia Ferreira de Oliveira
 * @since 01/09/2026
 */
public record AvaliacaoResponse(
        Long jogoId,
        String nomeJogo,
        Integer nota
) {

    public static AvaliacaoResponse fromDomain(Avaliacao avaliacao) {
        return new AvaliacaoResponse(
                avaliacao.getJogo().getId(),
                avaliacao.getJogo().getNome(),
                avaliacao.getNota()
        );
    }
}
