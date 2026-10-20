package com.playmatch.jogo.application;


import com.playmatch.jogo.infrastructure.client.rawg.RawgClient;
import com.playmatch.jogo.infrastructure.client.rawg.RawgJogoMapper;
import com.playmatch.jogo.infrastructure.dto.JogoPesquisaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The Service Class PesquisarJogosService
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
                .map(RawgJogoMapper::toPesquisaResponse)
                .toList();
    }
}
