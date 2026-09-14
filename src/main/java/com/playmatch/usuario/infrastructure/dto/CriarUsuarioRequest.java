package com.playmatch.usuario.infrastructure.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * The Record CriarUsuarioRequest
 *
 * @author Sofia Ferreira de Oliveira
 * @since 02/09/2026
 */

@Builder
public record CriarUsuarioRequest(
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "Email inválido")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 8, max = 72, message = "A senha deve conter entre 8 e 72 caracteres")
        String senha
) {
}
