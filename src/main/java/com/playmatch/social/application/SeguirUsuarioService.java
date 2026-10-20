package com.playmatch.social.application;


import com.playmatch.shared.application.security.UsuarioAutenticado;
import com.playmatch.shared.exception.BusinessException;
import com.playmatch.social.infrastructure.repository.SeguidorRepository;
import com.playmatch.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * The Service Class SeguirUsuarioService
 *
 * Seguir alguém que já é seguido não é erro: a operação é idempotente.
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */

@Service
@RequiredArgsConstructor
public class SeguirUsuarioService {

    private final SeguidorRepository seguidorRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioAutenticado usuarioAutenticado;

    public void executar(UUID usuarioSeguidoId) {
        UUID seguidorId = usuarioAutenticado.id();

        if (seguidorId.equals(usuarioSeguidoId)) {
            throw new BusinessException("Você não pode seguir a si mesmo", HttpStatus.UNPROCESSABLE_CONTENT);
        }
        if (!usuarioRepository.existsById(usuarioSeguidoId)) {
            throw new BusinessException("Usuário não encontrado", HttpStatus.NOT_FOUND);
        }

        seguidorRepository.seguir(seguidorId, usuarioSeguidoId);
    }
}
