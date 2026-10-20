package com.playmatch.social.infrastructure.dto;


import com.playmatch.social.domain.UsuarioConectado;

import java.time.Instant;
import java.util.UUID;

/**
 * The Record UsuarioConectadoResponse
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
public record UsuarioConectadoResponse(
        UUID usuarioId,
        String nome,
        Instant desde
) {

    public static UsuarioConectadoResponse fromDomain(UsuarioConectado usuario) {
        return new UsuarioConectadoResponse(usuario.usuarioId(), usuario.nome(), usuario.desde());
    }
}
