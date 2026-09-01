package com.playmatch.usuario.application;


import com.playmatch.shared.infrastructure.exception.UsuarioNaoEncontradoException;
import com.playmatch.usuario.domain.Usuario;
import com.playmatch.usuario.infrastructure.UsuarioRepository;
import com.playmatch.usuario.infrastructure.dto.AvaliacaoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The Domain Class ListarAvaliacaoService
 *
 * @author Sofia Ferreira de Oliveira
 * @since 01/09/2026
 */

@Service
@RequiredArgsConstructor
public class ListarAvaliacaoService {

    private final UsuarioRepository usuarioRepository;

    public List<AvaliacaoResponse> execute(Long usuarioId){
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(usuarioId));

        return usuario.getAvaliacoes()
                .stream()
                .map(avaliacao -> new AvaliacaoResponse(
                        avaliacao.getJogo().getId(),
                        avaliacao.getJogo().getNome(),
                        avaliacao.getNota()
                    ))
                .collect(java.util.stream.Collectors.toList());
    }
}
