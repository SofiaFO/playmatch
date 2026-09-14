package com.playmatch.usuario.application;


import com.playmatch.usuario.infrastructure.dto.UsuarioResponse;
import com.playmatch.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * The Service Class ListarUsuariosService
 *
 * @author Sofia Ferreira de Oliveira
 * @since 02/09/2026
 */

@Service
@RequiredArgsConstructor
public class ListarUsuariosService {

    private final UsuarioRepository usuarioRepository;

    public List<UsuarioResponse> executar() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioResponse::fromDomain)
                .sorted(Comparator.comparing(UsuarioResponse::nome))
                .toList();
    }
}
