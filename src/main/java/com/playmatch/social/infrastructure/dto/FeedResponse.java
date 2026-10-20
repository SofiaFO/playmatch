package com.playmatch.social.infrastructure.dto;


import java.util.List;

/**
 * The Record FeedResponse
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
public record FeedResponse(
        List<AtividadeResponse> itens,
        int pagina,
        int tamanho,
        boolean temMais
) {
}
