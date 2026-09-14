package com.playmatch.usuario.infrastructure.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * The Record AtualizarUsuarioRequest
 *
 * @author Sofia Ferreira de Oliveira
 * @since 02/09/2026
 */

public record AtualizarUsuarioRequest(


        @NotNull(message = "O ID do usuário é obrigatório")
        UUID id,

        String nome,

        @Email(message = "Email inválido")
        String email
) {
}
