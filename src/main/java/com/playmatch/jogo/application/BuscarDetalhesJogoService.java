package com.playmatch.jogo.application;


import com.playmatch.jogo.infrastructure.client.rawg.RawgClient;
import com.playmatch.jogo.infrastructure.client.rawg.dto.RawGeneroResponse;
import com.playmatch.jogo.infrastructure.dto.JogoDetalheResponse;
import com.playmatch.jogo.infrastructure.dto.JogoPesquisaResponse;
import com.playmatch.jogo.infrastructure.dto.RawgJogoDetalhesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * The Domain Class BuscarDetalhesJogoService
 *
 * @author Sofia Ferreira de Oliveira
 * @since 01/09/2026
 */
@Service
@RequiredArgsConstructor
public class BuscarDetalhesJogoService {

    private final RawgClient client;

    public JogoDetalheResponse execute(Long id){
        RawgJogoDetalhesResponse jogo = client.buscarPorId(id);

        return new JogoDetalheResponse(
                jogo.id(),
                jogo.name(),
                jogo.description(),
                jogo.released(),
                jogo.backgroundImage(),
                jogo.rating(),
                jogo.genres()
                        .stream()
                        .map(RawGeneroResponse::name)
                        .filter(Objects::nonNull)
                        .sorted()
                        .toList(),
                jogo.platforms()
                        .stream()
                        .map(platform -> platform.platform().name())
                        .filter(Objects::nonNull)
                        .sorted()
                        .toList()
        );
    }
}
