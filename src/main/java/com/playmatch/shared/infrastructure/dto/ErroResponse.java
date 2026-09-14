package com.playmatch.shared.infrastructure.dto;


import java.time.LocalDateTime;

/**
 * The DTO ErroResponse
 *
 * @author Sofia Ferreira de Oliveira
 * @since 31/08/2026
 */
public record ErroResponse(
        LocalDateTime timestamp,

        Integer status,

        String erro,

        String mensagem,

        String caminho
) {
}
