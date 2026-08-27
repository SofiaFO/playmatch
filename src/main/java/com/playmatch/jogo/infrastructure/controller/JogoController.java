package com.playmatch.jogo.infrastructure.controller;


import com.playmatch.jogo.application.BuscarJogoService;
import com.playmatch.jogo.application.ListarJogosService;
import com.playmatch.jogo.infrastructure.dto.JogoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping
    public List<JogoResponse> listar(){
        return listarJogosService.executar();
    }

    @GetMapping("/{id}")
    public JogoResponse buscarPorId(@PathVariable Long id){
        return buscarJogoService.executar(id);
    }
}
