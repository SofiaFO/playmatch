package com.playmatch.recomendacao.application;


import com.playmatch.recomendacao.domain.JogoRecomendado;
import com.playmatch.recomendacao.infrastructure.repository.RecomendacaoRepository;
import com.playmatch.shared.application.security.UsuarioAutenticado;
import com.playmatch.shared.exception.BusinessException;
import com.playmatch.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * The Service Class RecomendarJogosService
 *
 * Recomenda jogos parecidos com os que o usuário autenticado avaliou bem.
 * Quando o perfil ainda não rende recomendações suficientes (usuário novo ou
 * com poucas avaliações), completa a lista com os jogos mais populares.
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */

@Service
@RequiredArgsConstructor
public class RecomendarJogosService {

    static final int NOTA_MINIMA = 4;

    private final RecomendacaoRepository recomendacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioAutenticado usuarioAutenticado;

    public List<JogoRecomendado> executar(int limite) {
        UUID usuarioId = usuarioAutenticado.id();

        if (!usuarioRepository.existsById(usuarioId)) {
            throw new BusinessException("Usuário não encontrado", HttpStatus.NOT_FOUND);
        }

        List<JogoRecomendado> recomendacoes = new ArrayList<>(
                recomendacaoRepository.recomendarPorPerfil(usuarioId, NOTA_MINIMA, limite)
        );

        if (recomendacoes.size() < limite) {
            completarComPopulares(usuarioId, recomendacoes, limite);
        }

        return List.copyOf(recomendacoes);
    }

    private void completarComPopulares(UUID usuarioId, List<JogoRecomendado> recomendacoes, int limite) {
        Set<Long> jaRecomendados = new HashSet<>();
        recomendacoes.forEach(jogo -> jaRecomendados.add(jogo.jogoId()));

        recomendacaoRepository.buscarPopulares(usuarioId, limite).stream()
                .filter(jogo -> jaRecomendados.add(jogo.jogoId()))
                .limit(limite - recomendacoes.size())
                .forEach(recomendacoes::add);
    }
}
