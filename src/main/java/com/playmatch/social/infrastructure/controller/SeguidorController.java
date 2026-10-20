package com.playmatch.social.infrastructure.controller;


import com.playmatch.shared.infrastructure.dto.PaginaResponse;
import com.playmatch.social.application.DeixarDeSeguirUsuarioService;
import com.playmatch.social.application.ListarConexoesService;
import com.playmatch.social.application.SeguirUsuarioService;
import com.playmatch.social.infrastructure.dto.UsuarioConectadoResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * The Controller Class SeguidorController
 *
 * Seguir e deixar de seguir sempre agem em nome do usuário autenticado.
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
@RestController
@RequestMapping("/usuarios/{usuarioId}")
@RequiredArgsConstructor
public class SeguidorController {

    private final SeguirUsuarioService seguirUsuarioService;
    private final DeixarDeSeguirUsuarioService deixarDeSeguirUsuarioService;
    private final ListarConexoesService listarConexoesService;

    @PostMapping("/seguir")
    public ResponseEntity<Void> seguir(@PathVariable UUID usuarioId) {
        seguirUsuarioService.executar(usuarioId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/seguir")
    public ResponseEntity<Void> deixarDeSeguir(@PathVariable UUID usuarioId) {
        deixarDeSeguirUsuarioService.executar(usuarioId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/seguidores")
    public PaginaResponse<UsuarioConectadoResponse> listarSeguidores(
            @PathVariable UUID usuarioId,
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "A página deve ser no mínimo 0")
            int pagina,
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "O tamanho deve ser no mínimo 1")
            @Max(value = 50, message = "O tamanho deve ser no máximo 50")
            int tamanho
    ) {
        return listarConexoesService.listarSeguidores(usuarioId, pagina, tamanho);
    }

    @GetMapping("/seguindo")
    public PaginaResponse<UsuarioConectadoResponse> listarSeguindo(
            @PathVariable UUID usuarioId,
            @RequestParam(defaultValue = "0")
            @Min(value = 0, message = "A página deve ser no mínimo 0")
            int pagina,
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "O tamanho deve ser no mínimo 1")
            @Max(value = 50, message = "O tamanho deve ser no máximo 50")
            int tamanho
    ) {
        return listarConexoesService.listarSeguindo(usuarioId, pagina, tamanho);
    }
}
