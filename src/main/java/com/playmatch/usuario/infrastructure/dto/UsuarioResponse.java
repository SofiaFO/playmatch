package com.playmatch.usuario.infrastructure.dto;


import com.playmatch.usuario.domain.Usuario;
import lombok.Builder;

import java.util.UUID;

/**
 * The Record UsuarioResponse
 *
 * @author Sofia Ferreira de Oliveira
 * @since 02/09/2026
 */
@Builder
public record UsuarioResponse(
        UUID id,
        String nome,
        String email
) {

    public static UsuarioResponse fromDomain(Usuario usuario){
        return UsuarioResponse.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .build();
    }
}
