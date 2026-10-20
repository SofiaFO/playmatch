package com.playmatch.recomendacao.infrastructure.controller;


import com.playmatch.recomendacao.application.BuscarJogosSimilaresService;
import com.playmatch.recomendacao.application.BuscarUsuariosCompativeisService;
import com.playmatch.recomendacao.application.RecomendarJogosService;
import com.playmatch.recomendacao.application.RecomendarPorJogadoresParecidosService;
import com.playmatch.recomendacao.infrastructure.dto.JogoRecomendadoResponse;
import com.playmatch.recomendacao.infrastructure.dto.UsuarioCompativelResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * The Controller Class RecomendacaoController
 *
 * As recomendações pessoais são sempre do usuário autenticado (dono do token).
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
@RestController
@RequestMapping("/recomendacoes")
@RequiredArgsConstructor
public class RecomendacaoController {

    private final RecomendarJogosService recomendarJogosService;
    private final RecomendarPorJogadoresParecidosService recomendarPorJogadoresParecidosService;
    private final BuscarJogosSimilaresService buscarJogosSimilaresService;
    private final BuscarUsuariosCompativeisService buscarUsuariosCompativeisService;

    @GetMapping("/jogos")
    public List<JogoRecomendadoResponse> recomendarJogos(
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "O limite deve ser no mínimo 1")
            @Max(value = 50, message = "O limite deve ser no máximo 50")
            int limite
    ) {
        return recomendarJogosService.executar(limite).stream()
                .map(JogoRecomendadoResponse::fromDomain)
                .toList();
    }

    @GetMapping("/jogos/jogadores-parecidos")
    public List<JogoRecomendadoResponse> recomendarPorJogadoresParecidos(
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "O limite deve ser no mínimo 1")
            @Max(value = 50, message = "O limite deve ser no máximo 50")
            int limite
    ) {
        return recomendarPorJogadoresParecidosService.executar(limite).stream()
                .map(JogoRecomendadoResponse::fromDomain)
                .toList();
    }

    @GetMapping("/jogos/{jogoId}/similares")
    public List<JogoRecomendadoResponse> buscarSimilares(
            @PathVariable Long jogoId,
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "O limite deve ser no mínimo 1")
            @Max(value = 50, message = "O limite deve ser no máximo 50")
            int limite
    ) {
        return buscarJogosSimilaresService.executar(jogoId, limite).stream()
                .map(JogoRecomendadoResponse::fromDomain)
                .toList();
    }

    @GetMapping("/usuarios")
    public List<UsuarioCompativelResponse> buscarUsuariosCompativeis(
            @RequestParam(defaultValue = "10")
            @Min(value = 1, message = "O limite deve ser no mínimo 1")
            @Max(value = 50, message = "O limite deve ser no máximo 50")
            int limite
    ) {
        return buscarUsuariosCompativeisService.executar(limite).stream()
                .map(UsuarioCompativelResponse::fromDomain)
                .toList();
    }
}
