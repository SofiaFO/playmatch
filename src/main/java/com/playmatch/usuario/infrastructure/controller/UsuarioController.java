package com.playmatch.usuario.infrastructure.controller;

import com.playmatch.usuario.application.AvaliarJogoService;
import com.playmatch.usuario.infrastructure.dto.AvaliarJogoRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/{usuarioId}/avaliacoes")
    public ResponseEntity<Void> avaliarJogo(
            @PathVariable Long usuarioId,
            @Valid @RequestBody AvaliarJogoRequest request
    ) {
        avaliarJogoService.executar(usuarioId, request);

        return ResponseEntity.noContent().build();
    }
}