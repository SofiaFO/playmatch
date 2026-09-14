package com.playmatch.usuario.application.avaliacao;


import com.playmatch.jogo.infrastructure.repository.JogoRepository;
import com.playmatch.shared.exception.BusinessException;
import com.playmatch.usuario.domain.Usuario;
import com.playmatch.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * The Service Class DeletarAvaliacaoService
 *
 * @author Sofia Ferreira de Oliveira
 * @since 02/09/2026
 */

@Service
@RequiredArgsConstructor
public class DeletarAvaliacaoService {

    private final UsuarioRepository usuarioRepository;
    private final JogoRepository jogoRepository;

    @Transactional
    public void executar(UUID usuarioId, Long jogoId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado", HttpStatus.NOT_FOUND));

        jogoRepository.findById(jogoId)
                .orElseThrow(() -> new BusinessException("Jogo não encontrado", HttpStatus.NOT_FOUND));

        usuario.deletarAvaliacao(jogoId);

        usuarioRepository.save(usuario);
    }
}
