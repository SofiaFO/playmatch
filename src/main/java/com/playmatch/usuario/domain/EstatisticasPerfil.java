package com.playmatch.usuario.domain;


import java.util.List;

/**
 * The Domain Record EstatisticasPerfil
 *
 * @param generosFavoritos     até 3 gêneros mais frequentes entre os jogos favoritados ou bem avaliados
 * @param plataformasFavoritas até 3 plataformas mais frequentes entre esses mesmos jogos
 * @param visitanteSegue       se quem está vendo o perfil segue o dono do perfil
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
public record EstatisticasPerfil(
        long totalAvaliacoes,
        Double notaMedia,
        long totalFavoritos,
        long totalDesejos,
        long seguidores,
        long seguindo,
        List<String> generosFavoritos,
        List<String> plataformasFavoritas,
        boolean visitanteSegue
) {
}
