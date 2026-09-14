package com.playmatch.usuario.application;


import com.playmatch.shared.exception.BusinessException;
import com.playmatch.usuario.infrastructure.dto.UsuarioResponse;
import com.playmatch.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * The Service Class BuscarUsuarioService
 *
 * @author Sofia Ferreira de Oliveira
 * @since 02/09/2026
 */

@Service
@RequiredArgsConstructor
public class BuscarUsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioResponse executar(UUID usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .map(UsuarioResponse::fromDomain)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado", HttpStatus.NOT_FOUND));
    }
}
