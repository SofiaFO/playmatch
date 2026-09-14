package com.playmatch.usuario.infrastructure.controller;

import com.playmatch.usuario.application.*;
import com.playmatch.usuario.infrastructure.dto.AtualizarAvaliacaoRequest;
import com.playmatch.usuario.infrastructure.dto.AvaliacaoResponse;
import com.playmatch.usuario.infrastructure.dto.AvaliarJogoRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The Controller UsuarioController
 *
 * @author Sofia Ferreira de Oliveira
 * @since 01/09/2026
 */
@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final AvaliarJogoService avaliarJogoService;
    private final ListarAvaliacaoService listarAvaliacaoService;
    private final BuscarAvaliacaoService buscarAvaliacaoService;
    private final AtualizarAvaliacaoService atualizarAvaliacaoService;
    private final DeletarAvaliacaoService deletarAvaliacaoService;

    @PostMapping("/{usuarioId}/avaliacoes")
    public ResponseEntity<Void> avaliarJogo(
            @PathVariable Long usuarioId,
            @Valid @RequestBody AvaliarJogoRequest request
    ) {
        avaliarJogoService.executar(usuarioId, request);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{usuarioId}/avaliacoes")
    public List<AvaliacaoResponse> listarAvaliacoes(@PathVariable Long usuarioId){
        return listarAvaliacaoService.execute(usuarioId);
    }

    @GetMapping("/{usuarioId}/avaliacoes/{jogoId}")
    public AvaliacaoResponse buscarAvaliacao(@PathVariable Long usuarioId, @PathVariable Long jogoId) {
        return buscarAvaliacaoService.executar(usuarioId, jogoId);
    }

    @PutMapping("/{usuarioId}/avaliacoes/{jogoId}")
    public ResponseEntity<Void> atualizarAvaliacao(@PathVariable Long usuarioId, @PathVariable Long jogoId, @Valid @RequestBody AtualizarAvaliacaoRequest request) {
        atualizarAvaliacaoService.executar(usuarioId, jogoId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{usuarioId}/avaliacoes/{jogoId}")
    public ResponseEntity<Void> deletarAvaliacao(@PathVariable Long usuarioId, @PathVariable Long jogoId) {
        deletarAvaliacaoService.executar(usuarioId, jogoId);
        return ResponseEntity.noContent().build();
    }
}