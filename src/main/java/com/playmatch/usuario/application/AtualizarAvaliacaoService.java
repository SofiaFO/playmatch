package com.playmatch.usuario.application;


import com.playmatch.jogo.infrastructure.JogoRepository;
import com.playmatch.shared.exception.BusinessException;
import com.playmatch.usuario.domain.Usuario;
import com.playmatch.usuario.infrastructure.UsuarioRepository;
import com.playmatch.usuario.infrastructure.dto.AtualizarAvaliacaoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Domain Class AtualizarAvaliacaoService
 *
 * @author Sofia Ferreira de Oliveira
 * @since 02/09/2026
 */

@Service
@RequiredArgsConstructor
public class AtualizarAvaliacaoService {
    private final UsuarioRepository usuarioRepository;
    private final JogoRepository jogoRepository;

    @Transactional
    public void executar(Long usuarioId, Long jogoId, AtualizarAvaliacaoRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado", HttpStatus.NOT_FOUND));

        jogoRepository.findById(jogoId)
                .orElseThrow(() -> new BusinessException("Jogo não encontrado", HttpStatus.NOT_FOUND));

        usuario.atualizarAvaliacao(jogoId, request.nota());

        usuarioRepository.save(usuario);
    }
}
