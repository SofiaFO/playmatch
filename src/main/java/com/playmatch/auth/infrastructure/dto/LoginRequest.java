package com.playmatch.auth.infrastructure.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * The Request LoginRequest
 *
 * @author Sofia Ferreira de Oliveira
 * @since 24/09/2026
 */
public record LoginRequest(
        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Email inválido")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        String senha
) {
}
