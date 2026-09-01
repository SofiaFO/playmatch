package com.playmatch.usuario.infrastructure.controller;

import com.playmatch.usuario.application.AvaliarJogoService;
import com.playmatch.usuario.application.ListarAvaliacaoService;
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
}