package com.playmatch.jogo.infrastructure.client.rawg;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * The Domain Class RawgClient
 *
 * @author Sofia Ferreira de Oliveira
 * @since 27/08/2026
 */

@Component
@RequiredArgsConstructor
public class RawgClient {

    @Value("${rawg.base-url}")
    private String baseUrl;

    @Value("${rawg.api-key}")
    private String apiKey;

    private final RestClient restClient;
}
