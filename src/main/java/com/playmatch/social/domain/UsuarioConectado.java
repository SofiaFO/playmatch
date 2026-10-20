package com.playmatch.social.domain;


import java.time.Instant;
import java.util.UUID;

/**
 * The Domain Record UsuarioConectado
 *
 * Um seguidor ou um usuário seguido, com a data em que a conexão começou.
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
public record UsuarioConectado(
        UUID usuarioId,
        String nome,
        Instant desde
) {
}
