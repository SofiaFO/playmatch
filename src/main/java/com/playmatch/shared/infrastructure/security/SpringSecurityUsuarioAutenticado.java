package com.playmatch.shared.infrastructure.security;


import com.playmatch.shared.application.security.UsuarioAutenticado;
import com.playmatch.shared.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * The Adapter Class SpringSecurityUsuarioAutenticado
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */

@Component
public class SpringSecurityUsuarioAutenticado implements UsuarioAutenticado {

    @Override
    public UUID id() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UUID usuarioId)) {
            throw new BusinessException("Usuário não autenticado", HttpStatus.UNAUTHORIZED);
        }

        return usuarioId;
    }
}
