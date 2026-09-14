package com.playmatch.usuario.application;


import com.playmatch.shared.exception.BusinessException;
import com.playmatch.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * The Service Class DeletarUsuarioService
 *
 * @author Sofia Ferreira de Oliveira
 * @since 02/09/2026
 */

@Service
@RequiredArgsConstructor
public class DeletarUsuarioService {
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public void executar(UUID usuarioId) {
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado", HttpStatus.NOT_FOUND));

        usuarioRepository.deleteById(usuarioId);
    }
}

