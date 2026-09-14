package com.playmatch.usuario.infrastructure.controller;

import com.playmatch.usuario.application.AtualizarUsuarioService;
import com.playmatch.usuario.application.BuscarUsuarioService;
import com.playmatch.usuario.application.CriarUsuarioService;
import com.playmatch.usuario.application.DeletarUsuarioService;
import com.playmatch.usuario.application.ListarUsuariosService;
import com.playmatch.usuario.infrastructure.dto.AtualizarUsuarioRequest;
import com.playmatch.usuario.infrastructure.dto.CriarUsuarioRequest;
import com.playmatch.usuario.infrastructure.dto.UsuarioResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * The Controller Class UsuarioController
 *
 * @author Sofia Ferreira de Oliveira
 * @since 01/09/2026
 */
@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final CriarUsuarioService criarUsuarioService;
    private final AtualizarUsuarioService atualizarUsuarioService;
    private final BuscarUsuarioService buscarUsuarioService;
    private final ListarUsuariosService listarUsuariosService;
    private final DeletarUsuarioService deletarUsuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponse> criarUsuario(@Valid @RequestBody CriarUsuarioRequest request) {
        UsuarioResponse response = criarUsuarioService.executar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<UsuarioResponse> listarUsuarios() {
        return listarUsuariosService.executar();
    }

    @GetMapping("/{usuarioId}")
    public UsuarioResponse buscarUsuario(@PathVariable UUID usuarioId) {
        return buscarUsuarioService.executar(usuarioId);
    }

    @PutMapping
    public ResponseEntity<Void> atualizarUsuario(@Valid @RequestBody AtualizarUsuarioRequest request) {
        atualizarUsuarioService.executar(request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable UUID usuarioId) {
        deletarUsuarioService.executar(usuarioId);
        return ResponseEntity.noContent().build();
    }
}
