package com.playmatch.social.application;


import com.playmatch.shared.application.security.UsuarioAutenticado;
import com.playmatch.shared.exception.BusinessException;
import com.playmatch.social.infrastructure.repository.SeguidorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * The Service Class DeixarDeSeguirUsuarioService
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */

@Service
@RequiredArgsConstructor
public class DeixarDeSeguirUsuarioService {

    private final SeguidorRepository seguidorRepository;
    private final UsuarioAutenticado usuarioAutenticado;

    public void executar(UUID usuarioSeguidoId) {
        if (!seguidorRepository.deixarDeSeguir(usuarioAutenticado.id(), usuarioSeguidoId)) {
            throw new BusinessException("Você não segue este usuário", HttpStatus.NOT_FOUND);
        }
    }
}
