package com.playmatch.jogo.application;


import com.playmatch.jogo.infrastructure.client.rawg.RawgClient;
import com.playmatch.jogo.infrastructure.client.rawg.dto.RawGeneroResponse;
import com.playmatch.jogo.infrastructure.client.rawg.dto.RawPlataformaResponse;
import com.playmatch.jogo.infrastructure.dto.JogoPesquisaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * The Domain Class PesquisarJogosService
 *
 * @author Sofia Ferreira de Oliveira
 * @since 27/08/2026
 */

@Service
@RequiredArgsConstructor
public class PesquisarJogosService {
    private final RawgClient rawgClient;

    public List<JogoPesquisaResponse> executar(String nome) {
        return rawgClient.pesquisar(nome)
                .results()
                .stream()
                .map(jogo -> new JogoPesquisaResponse(
                        jogo.id(),
                        jogo.name(),
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
                                .map(RawPlataformaResponse::name)
                                .filter(Objects::nonNull)
                                .sorted()
                                .toList()
                ))
                .toList();
    }
}
