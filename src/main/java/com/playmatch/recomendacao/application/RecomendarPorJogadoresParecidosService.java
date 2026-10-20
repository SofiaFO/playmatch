package com.playmatch.recomendacao.application;


import com.playmatch.recomendacao.domain.JogoRecomendado;
import com.playmatch.recomendacao.infrastructure.repository.RecomendacaoRepository;
import com.playmatch.shared.application.security.UsuarioAutenticado;
import com.playmatch.shared.exception.BusinessException;
import com.playmatch.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * The Service Class RecomendarPorJogadoresParecidosService
 *
 * Filtragem colaborativa: "quem curtiu o que você curtiu também curtiu...".
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */

@Service
@RequiredArgsConstructor
public class RecomendarPorJogadoresParecidosService {

    private final RecomendacaoRepository recomendacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioAutenticado usuarioAutenticado;

    public List<JogoRecomendado> executar(int limite) {
        UUID usuarioId = usuarioAutenticado.id();

        if (!usuarioRepository.existsById(usuarioId)) {
            throw new BusinessException("Usuário não encontrado", HttpStatus.NOT_FOUND);
        }

        return recomendacaoRepository.recomendarPorJogadoresParecidos(
                usuarioId,
                RecomendarJogosService.NOTA_MINIMA,
                limite
        );
    }
}
