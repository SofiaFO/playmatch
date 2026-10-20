package com.playmatch.social.application;


import com.playmatch.shared.exception.BusinessException;
import com.playmatch.shared.infrastructure.dto.PaginaResponse;
import com.playmatch.social.infrastructure.dto.UsuarioConectadoResponse;
import com.playmatch.social.infrastructure.repository.SeguidorRepository;
import com.playmatch.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * The Service Class ListarConexoesService
 *
 * Lista os seguidores de um usuário e quem ele segue.
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */

@Service
@RequiredArgsConstructor
public class ListarConexoesService {

    private final SeguidorRepository seguidorRepository;
    private final UsuarioRepository usuarioRepository;

    public PaginaResponse<UsuarioConectadoResponse> listarSeguidores(UUID usuarioId, int pagina, int tamanho) {
        validarUsuario(usuarioId);

        return PaginaResponse.de(
                seguidorRepository.listarSeguidores(usuarioId, pagina, tamanho),
                pagina,
                tamanho,
                seguidorRepository.contarSeguidores(usuarioId),
                UsuarioConectadoResponse::fromDomain
        );
    }

    public PaginaResponse<UsuarioConectadoResponse> listarSeguindo(UUID usuarioId, int pagina, int tamanho) {
        validarUsuario(usuarioId);

        return PaginaResponse.de(
                seguidorRepository.listarSeguindo(usuarioId, pagina, tamanho),
                pagina,
                tamanho,
                seguidorRepository.contarSeguindo(usuarioId),
                UsuarioConectadoResponse::fromDomain
        );
    }

    private void validarUsuario(UUID usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new BusinessException("Usuário não encontrado", HttpStatus.NOT_FOUND);
        }
    }
}
