package com.playmatch.jogo.infrastructure.controller;


import com.playmatch.jogo.application.BuscarDetalhesJogoService;
import com.playmatch.jogo.application.BuscarJogoService;
import com.playmatch.jogo.application.ImportarJogoService;
import com.playmatch.jogo.application.ListarAvaliacoesDoJogoService;
import com.playmatch.jogo.application.ListarJogosService;
import com.playmatch.jogo.application.PesquisarJogosService;
import com.playmatch.jogo.application.ResultadoImportacao;
import com.playmatch.jogo.infrastructure.dto.AvaliacaoDoJogoResponse;
import com.playmatch.jogo.infrastructure.dto.JogoDetalheResponse;
import com.playmatch.jogo.infrastructure.dto.JogoPesquisaResponse;
import com.playmatch.jogo.infrastructure.dto.JogoResponse;
import com.playmatch.shared.infrastructure.dto.PaginaResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * The Controller Class JogoController
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
    private final ImportarJogoService importarJogoService;
    private final ListarAvaliacoesDoJogoService listarAvaliacoesDoJogoService;

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
        return buscarDetalhesJogoService.executar(id);
    }

    @PostMapping("/importar/{rawgId}")
    public ResponseEntity<JogoResponse> importar(@PathVariable Long rawgId) {
        ResultadoImportacao resultado = importarJogoService.executar(rawgId);
        JogoResponse response = buscarJogoService.executar(resultado.jogo().getId());

        if (!resultado.criado()) {
            return ResponseEntity.ok(response);
        }

        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/jogos/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}/avaliacoes")
    public PaginaResponse<AvaliacaoDoJogoResponse> listarAvaliacoes(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean comResenha,
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "A página deve ser no mínimo 0")
            int pagina,
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "O tamanho deve ser no mínimo 1")
            @Max(value = 50, message = "O tamanho deve ser no máximo 50")
            int tamanho
    ) {
        return listarAvaliacoesDoJogoService.executar(id, comResenha, pagina, tamanho);
    }
}
