package com.playmatch.shared.infrastructure.dto;


import java.util.List;
import java.util.function.Function;

/**
 * The DTO PaginaResponse
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
public record PaginaResponse<T>(
        List<T> itens,
        int pagina,
        int tamanho,
        long totalItens,
        int totalPaginas
) {

    public static <D, T> PaginaResponse<T> de(
            List<D> itens,
            int pagina,
            int tamanho,
            long totalItens,
            Function<D, T> conversor
    ) {
        int totalPaginas = (int) ((totalItens + tamanho - 1) / tamanho);

        return new PaginaResponse<>(
                itens.stream().map(conversor).toList(),
                pagina,
                tamanho,
                totalItens,
                totalPaginas
        );
    }
}
