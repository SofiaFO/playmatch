package com.playmatch.usuario.infrastructure.controller;


import com.playmatch.usuario.application.perfil.BuscarPerfilService;
import com.playmatch.usuario.infrastructure.dto.perfil.PerfilResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * The Controller Class PerfilController
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class PerfilController {

    private final BuscarPerfilService buscarPerfilService;

    @GetMapping("/me/perfil")
    public PerfilResponse buscarMeuPerfil() {
        return buscarPerfilService.executarParaUsuarioAutenticado();
    }

    @GetMapping("/{usuarioId}/perfil")
    public PerfilResponse buscarPerfil(@PathVariable UUID usuarioId) {
        return buscarPerfilService.executar(usuarioId);
    }
}
