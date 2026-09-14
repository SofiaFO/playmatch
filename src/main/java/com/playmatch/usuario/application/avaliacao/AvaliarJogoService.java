package com.playmatch.usuario.application.avaliacao;


import com.playmatch.jogo.domain.Jogo;
import com.playmatch.jogo.infrastructure.repository.JogoRepository;
import com.playmatch.shared.exception.BusinessException;
import com.playmatch.usuario.domain.Usuario;
import com.playmatch.usuario.infrastructure.dto.avaliacao.AvaliarJogoRequest;
import com.playmatch.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Service Class AvaliarJogoService
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
    public void executar(AvaliarJogoRequest request){
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado", HttpStatus.NOT_FOUND));


        Jogo jogo = jogoRepository.findById(request.jogoId())
                .orElseThrow(() -> new BusinessException("Jogo não encontrado", HttpStatus.NOT_FOUND));

        usuario.avaliar(jogo, request.nota());

        usuarioRepository.save(usuario);
    }
}
