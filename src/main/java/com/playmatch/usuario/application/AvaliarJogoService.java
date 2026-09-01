package com.playmatch.usuario.application;


import com.playmatch.jogo.domain.Jogo;
import com.playmatch.jogo.infrastructure.JogoRepository;
import com.playmatch.shared.infrastructure.exception.JogoNaoEncontradoException;
import com.playmatch.shared.infrastructure.exception.UsuarioNaoEncontradoException;
import com.playmatch.usuario.domain.Usuario;
import com.playmatch.usuario.infrastructure.UsuarioRepository;
import com.playmatch.usuario.infrastructure.dto.AvaliarJogoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Application Class AvaliarJogoService
 *
 * @author Sofia Ferreira de Oliveira
 * @since 01/09/2026
 */

@Service
@RequiredArgsConstructor
public class AvaliarJogoService {

    private final UsuarioRepository usuarioRepository;
    private final JogoRepository jogoRepository;

    @Transactional
    public void executar(Long usuarioId, AvaliarJogoRequest request){
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(usuarioId));


        Jogo jogo = jogoRepository.findById(request.jogoId())
                .orElseThrow(() -> new JogoNaoEncontradoException(request.jogoId()));

        usuario.avaliar(jogo, request.nota());

        usuarioRepository.save(usuario);
    }
}
