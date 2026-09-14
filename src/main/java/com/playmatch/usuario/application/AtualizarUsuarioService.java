package com.playmatch.usuario.application;


import com.playmatch.shared.exception.BusinessException;
import com.playmatch.usuario.domain.Usuario;
import com.playmatch.usuario.infrastructure.dto.AtualizarUsuarioRequest;
import com.playmatch.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * The Service Class AtualizarUsuarioService
 *
 * @author Sofia Ferreira de Oliveira
 * @since 02/09/2026
 */

@Service
@RequiredArgsConstructor
public class AtualizarUsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional
    public void executar(AtualizarUsuarioRequest request){

        Usuario usuario = usuarioRepository.findById(request.id())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado", HttpStatus.NOT_FOUND));

        if (request.email() != null
                && usuarioRepository.existsByEmailAndIdNot(request.email(), request.id())) {
            throw new BusinessException("Email já cadastrado", HttpStatus.CONFLICT);
        }

        usuario.atualizarDados(request.nome(), request.email());

        usuarioRepository.save(usuario);
    }
}
