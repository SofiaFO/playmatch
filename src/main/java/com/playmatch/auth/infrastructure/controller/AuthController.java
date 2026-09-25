package com.playmatch.auth.infrastructure.controller;

import com.playmatch.auth.application.AutenticarUsuarioService;
import com.playmatch.auth.infrastructure.dto.LoginRequest;
import com.playmatch.auth.infrastructure.dto.LoginResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Controller Class AuthController
 *
 * @author Sofia Ferreira de Oliveira
 * @since 24/09/2026
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AutenticarUsuarioService autenticarUsuarioService;

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return autenticarUsuarioService.executar(request);
    }
}
