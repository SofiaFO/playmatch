package com.playmatch.jogo.infrastructure.client.rawg;

import com.playmatch.jogo.infrastructure.client.rawg.dto.RawgPesquisaResponse;
import com.playmatch.jogo.infrastructure.dto.RawgJogoDetalhesResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * The Client RawgClient
 *
 * @author Sofia Ferreira de Oliveira
 * @since 27/08/2026
 */
@Component
public class RawgClient {

    private final RestClient restClient;
    private final String apiKey;

    public RawgClient(
            RestClient.Builder builder,
            @Value("${rawg.base-url}") String baseUrl,
            @Value("${rawg.api-key}") String apiKey
    ) {
        this.restClient = builder
                .baseUrl(baseUrl)
                .build();

        this.apiKey = apiKey;
    }

    public RawgPesquisaResponse pesquisar(String nome){
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/games")
                        .queryParam("key", apiKey)
                        .queryParam("search", nome)
                        .queryParam("search_precise", true)
                        .queryParam("page_size", 10)
                        .build())
                .retrieve()
                .body(RawgPesquisaResponse.class);
    }

    public RawgJogoDetalhesResponse buscarPorId(Long rawgId){
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/games/{id}")
                            .queryParam("key", apiKey)
                            .build(rawgId))
                    .retrieve()
                    .body(RawgJogoDetalhesResponse.class);
    }
}