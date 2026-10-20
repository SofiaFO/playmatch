package com.playmatch.jogo.application;


import com.playmatch.jogo.infrastructure.client.rawg.RawgClient;
import com.playmatch.jogo.infrastructure.client.rawg.RawgJogoMapper;
import com.playmatch.jogo.infrastructure.dto.JogoDetalheResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * The Service Class BuscarDetalhesJogoService
 *
 * @author Sofia Ferreira de Oliveira
 * @since 01/09/2026
 */
@Service
@RequiredArgsConstructor
public class BuscarDetalhesJogoService {

    private final RawgClient client;

    public JogoDetalheResponse executar(Long id){
        return RawgJogoMapper.toDetalheResponse(client.buscarPorId(id));
    }
}
