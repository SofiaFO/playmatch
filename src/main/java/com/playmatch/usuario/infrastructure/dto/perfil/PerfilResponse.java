package com.playmatch.usuario.infrastructure.dto.perfil;


import com.playmatch.usuario.infrastructure.dto.avaliacao.AvaliacaoResponse;

import java.util.List;
import java.util.UUID;

/**
 * The Record PerfilResponse
 *
 * @param voceSegue {@code null} quando o perfil é do próprio usuário autenticado
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
public record PerfilResponse(
        UUID usuarioId,
        String nome,
        boolean ehVoce,
        Boolean voceSegue,
        long totalAvaliacoes,
        Double notaMedia,
        long totalFavoritos,
        long totalDesejos,
        long seguidores,
        long seguindo,
        List<String> generosFavoritos,
        List<String> plataformasFavoritas,
        List<AvaliacaoResponse> ultimasAvaliacoes
) {
}
