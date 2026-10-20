package com.playmatch.recomendacao.infrastructure.dto;


import com.playmatch.recomendacao.domain.UsuarioCompativel;

import java.util.List;
import java.util.UUID;

/**
 * The Record UsuarioCompativelResponse
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
public record UsuarioCompativelResponse(
        UUID usuarioId,
        String nome,
        Long jogosEmComum,
        Integer compatibilidade,
        List<String> exemplos
) {

    public static UsuarioCompativelResponse fromDomain(UsuarioCompativel usuario) {
        return new UsuarioCompativelResponse(
                usuario.usuarioId(),
                usuario.nome(),
                usuario.jogosEmComum(),
                usuario.compatibilidade(),
                usuario.exemplos()
        );
    }
}
