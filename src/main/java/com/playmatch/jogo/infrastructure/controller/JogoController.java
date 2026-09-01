package com.playmatch.jogo.infrastructure.controller;


import com.playmatch.jogo.application.BuscarDetalhesJogoService;
import com.playmatch.jogo.application.BuscarJogoService;
import com.playmatch.jogo.application.ListarJogosService;
import com.playmatch.jogo.application.PesquisarJogosService;
import com.playmatch.jogo.infrastructure.dto.JogoDetalheResponse;
import com.playmatch.jogo.infrastructure.dto.JogoPesquisaResponse;
import com.playmatch.jogo.infrastructure.dto.JogoResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The Domain Class JogoController
 *
 * @author Sofia Ferreira de Oliveira
 * @since 26/08/2026
 */

@RestController
@RequestMapping("/jogos")
@RequiredArgsConstructor
public class JogoController {

    private final ListarJogosService listarJogosService;
    private final BuscarJogoService buscarJogoService;
    private final PesquisarJogosService pesquisarJogosService;
    private final BuscarDetalhesJogoService buscarDetalhesJogoService;

    @GetMapping
    public List<JogoResponse> listar() {
        return listarJogosService.executar();
    }

    @GetMapping("/{id}")
    public JogoResponse buscarPorId(@PathVariable Long id) {
        return buscarJogoService.executar(id);
    }

    @Validated
    @GetMapping("/pesquisar")
    public List<JogoPesquisaResponse> pesquisar(
            @RequestParam
            @NotBlank(message = "O nome do jogo é obrigatório")
            @Size(min = 2, message = "O nome deve possuir pelo menos 2 caracteres")
            String nome
    ) {
        return pesquisarJogosService.executar(nome);
    }

    @GetMapping("/externos/{id}")
    public JogoDetalheResponse pesquisaExterno(@PathVariable Long id){
        return buscarDetalhesJogoService.execute(id);
    }
}
