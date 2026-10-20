package com.playmatch.jogo.infrastructure.client.rawg;


import com.playmatch.jogo.domain.Jogo;
import com.playmatch.jogo.infrastructure.client.rawg.dto.RawGeneroResponse;
import com.playmatch.jogo.infrastructure.client.rawg.dto.RawJogoPlataformaResponse;
import com.playmatch.jogo.infrastructure.client.rawg.dto.RawPlataformaResponse;
import com.playmatch.jogo.infrastructure.client.rawg.dto.RawgJogoDetalhesResponse;
import com.playmatch.jogo.infrastructure.client.rawg.dto.RawgJogoResponse;
import com.playmatch.jogo.infrastructure.dto.JogoDetalheResponse;
import com.playmatch.jogo.infrastructure.dto.JogoPesquisaResponse;

import java.util.List;
import java.util.Objects;

/**
 * The Mapper RawgJogoMapper
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
public final class RawgJogoMapper {

    private RawgJogoMapper() {
    }

    public static JogoPesquisaResponse toPesquisaResponse(RawgJogoResponse jogo) {
        return new JogoPesquisaResponse(
                jogo.id(),
                jogo.name(),
                jogo.released(),
                jogo.backgroundImage(),
                jogo.rating(),
                nomesGeneros(jogo.genres()),
                nomesPlataformas(jogo.platforms())
        );
    }

    public static JogoDetalheResponse toDetalheResponse(RawgJogoDetalhesResponse jogo) {
        return new JogoDetalheResponse(
                jogo.id(),
                jogo.name(),
                jogo.description(),
                jogo.released(),
                jogo.backgroundImage(),
                jogo.rating(),
                nomesGeneros(jogo.genres()),
                nomesPlataformas(jogo.platforms())
        );
    }

    public static Jogo toDomain(RawgJogoDetalhesResponse jogo) {
        return Jogo.importar(
                jogo.id(),
                jogo.name(),
                jogo.description(),
                jogo.released(),
                jogo.backgroundImage(),
                jogo.rating(),
                nomesGeneros(jogo.genres()),
                nomesPlataformas(jogo.platforms())
        );
    }

    private static List<String> nomesGeneros(List<RawGeneroResponse> generos) {
        return generos.stream()
                .filter(Objects::nonNull)
                .map(RawGeneroResponse::name)
                .filter(Objects::nonNull)
                .sorted()
                .toList();
    }

    private static List<String> nomesPlataformas(List<RawJogoPlataformaResponse> plataformas) {
        return plataformas.stream()
                .filter(Objects::nonNull)
                .map(RawJogoPlataformaResponse::platform)
                .filter(Objects::nonNull)
                .map(RawPlataformaResponse::name)
                .filter(Objects::nonNull)
                .sorted()
                .toList();
    }
}
