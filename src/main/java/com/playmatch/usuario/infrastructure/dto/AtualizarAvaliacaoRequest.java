package com.playmatch.usuario.infrastructure.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * The Record AtualizarAvaliacaoRequest
 *
 * @author Sofia Ferreira de Oliveira
 * @since 02/09/2026
 */
public record AtualizarAvaliacaoRequest(

        @NotNull
        @Min(value = 1, message = "A nota deve ser no mínimo 1")
        @Max(value = 5, message = "A nota deve ser no máximo 5")
        Integer nota
){}
