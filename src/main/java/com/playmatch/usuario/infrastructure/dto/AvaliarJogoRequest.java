package com.playmatch.usuario.infrastructure.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * The Request AvaliarJogoRequest
 *
 * @author Sofia Ferreira de Oliveira
 * @since 01/09/2026
 */
public record AvaliarJogoRequest(
        @NotNull(message = "O ID do jogo é obrigatório")
        Long jogoId,

        @NotNull(message = "A nota não pode ser nula")
        @Min(value = 1, message = "A nota mínima é 1")
        @Max(value = 5, message = "A nota máxima é 5")
        Integer nota
) {
}
