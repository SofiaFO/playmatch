package com.playmatch.usuario.application;


import com.playmatch.jogo.infrastructure.JogoRepository;
import com.playmatch.shared.exception.BusinessException;
import com.playmatch.usuario.domain.Usuario;
import com.playmatch.usuario.infrastructure.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * The Domain Class DeletarAvaliacaoService
 *
 * @author Sofia Ferreira de Oliveira
 * @since 02/09/2026
 */

@Service
@RequiredArgsConstructor
public class DeletarAvaliacaoService {

    private final UsuarioRepository usuarioRepository;
    private final JogoRepository jogoRepository;

    public void executar(Long usuarioId, Long jogoId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado", HttpStatus.NOT_FOUND));

        jogoRepository.findById(jogoId)
                .orElseThrow(() -> new BusinessException("Jogo não encontrado", HttpStatus.NOT_FOUND));

        usuario.deletarAvaliacao(jogoId);

        usuarioRepository.save(usuario);
    }
}
