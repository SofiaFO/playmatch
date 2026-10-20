package com.playmatch.jogo.infrastructure.client.rawg;

import com.playmatch.jogo.infrastructure.client.rawg.dto.RawgJogoDetalhesResponse;
import com.playmatch.jogo.infrastructure.client.rawg.dto.RawgPesquisaResponse;
import com.playmatch.shared.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.function.Supplier;

/**
 * The Client RawgClient
 *
 * @author Sofia Ferreira de Oliveira
 * @since 27/08/2026
 */
@Component
public class RawgClient {

    private static final String MENSAGEM_FALHA = "Não foi possível consultar o catálogo de jogos no momento";

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
        RawgPesquisaResponse response = chamar(() -> restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/games")
                        .queryParam("key", apiKey)
                        .queryParam("search", nome)
                        .queryParam("search_precise", true)
                        .queryParam("page_size", 10)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::isError, (request, resposta) -> {
                    throw new BusinessException(MENSAGEM_FALHA, HttpStatus.BAD_GATEWAY);
                })
                .body(RawgPesquisaResponse.class));

        return response != null ? response : new RawgPesquisaResponse(0, List.of());
    }

    public RawgJogoDetalhesResponse buscarPorId(Long rawgId){
        RawgJogoDetalhesResponse response = chamar(() -> restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/games/{id}")
                        .queryParam("key", apiKey)
                        .build(rawgId))
                .retrieve()
                .onStatus(status -> status.value() == HttpStatus.NOT_FOUND.value(), (request, resposta) -> {
                    throw new BusinessException("Jogo não encontrado no catálogo externo", HttpStatus.NOT_FOUND);
                })
                .onStatus(HttpStatusCode::isError, (request, resposta) -> {
                    throw new BusinessException(MENSAGEM_FALHA, HttpStatus.BAD_GATEWAY);
                })
                .body(RawgJogoDetalhesResponse.class));

        if (response == null) {
            throw new BusinessException(MENSAGEM_FALHA, HttpStatus.BAD_GATEWAY);
        }

        return response;
    }

    private <T> T chamar(Supplier<T> requisicao) {
        try {
            return requisicao.get();
        } catch (RestClientException exception) {
            throw new BusinessException(MENSAGEM_FALHA, HttpStatus.BAD_GATEWAY);
        }
    }
}
