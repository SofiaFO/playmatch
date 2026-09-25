package com.playmatch.auth.application;

import com.playmatch.auth.infrastructure.dto.LoginRequest;
import com.playmatch.auth.infrastructure.dto.LoginResponse;
import com.playmatch.shared.application.security.SenhaEncoder;
import com.playmatch.shared.exception.BusinessException;
import com.playmatch.shared.infrastructure.security.JwtService;
import com.playmatch.usuario.domain.Usuario;
import com.playmatch.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * The Service Class AutenticarUsuarioService
 *
 * @author Sofia Ferreira de Oliveira
 * @since 24/09/2026
 */

@Service
@RequiredArgsConstructor
public class AutenticarUsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final SenhaEncoder senhaEncoder;
    private final JwtService jwtService;

    public LoginResponse executar(LoginRequest request) {
        String email = Usuario.normalizarEmail(request.email());

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Credenciais inválidas", HttpStatus.UNAUTHORIZED));

        if (!senhaEncoder.corresponde(request.senha(), usuario.getSenhaHash())) {
            throw new BusinessException("Credenciais inválidas", HttpStatus.UNAUTHORIZED);
        }

        return new LoginResponse(jwtService.gerarToken(usuario.getEmail()));
    }
}
