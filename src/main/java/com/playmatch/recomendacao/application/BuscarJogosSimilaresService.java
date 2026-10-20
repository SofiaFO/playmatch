package com.playmatch.recomendacao.application;


import com.playmatch.jogo.infrastructure.repository.JogoRepository;
import com.playmatch.recomendacao.domain.JogoRecomendado;
import com.playmatch.recomendacao.infrastructure.repository.RecomendacaoRepository;
import com.playmatch.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * The Service Class BuscarJogosSimilaresService
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */

@Service
@RequiredArgsConstructor
public class BuscarJogosSimilaresService {

    private final RecomendacaoRepository recomendacaoRepository;
    private final JogoRepository jogoRepository;

    public List<JogoRecomendado> executar(Long jogoId, int limite) {
        if (!jogoRepository.existsById(jogoId)) {
            throw new BusinessException("Jogo não encontrado", HttpStatus.NOT_FOUND);
        }

        return recomendacaoRepository.buscarSimilares(jogoId, limite);
    }
}
