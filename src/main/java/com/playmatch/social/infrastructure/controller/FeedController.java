package com.playmatch.social.infrastructure.controller;


import com.playmatch.social.application.BuscarFeedService;
import com.playmatch.social.infrastructure.dto.FeedResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Controller Class FeedController
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {

    private final BuscarFeedService buscarFeedService;

    @GetMapping
    public FeedResponse buscarFeed(
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "A página deve ser no mínimo 0")
            int pagina,
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "O tamanho deve ser no mínimo 1")
            @Max(value = 50, message = "O tamanho deve ser no máximo 50")
            int tamanho
    ) {
        return buscarFeedService.executar(pagina, tamanho);
    }
}
