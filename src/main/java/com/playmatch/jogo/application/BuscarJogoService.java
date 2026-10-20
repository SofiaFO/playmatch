package com.playmatch.jogo.application;


import com.playmatch.jogo.infrastructure.dto.JogoResponse;
import com.playmatch.jogo.infrastructure.repository.AvaliacaoJogoConsultaRepository;
import com.playmatch.jogo.infrastructure.repository.JogoRepository;
import com.playmatch.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * The Service Class BuscarJogoService
 *
 * @author Sofia Ferreira de Oliveira
 * @since 27/08/2026
 */

@Service
@RequiredArgsConstructor
public class BuscarJogoService {

    private final JogoRepository repository;
    private final AvaliacaoJogoConsultaRepository avaliacaoJogoConsultaRepository;

    public JogoResponse executar(Long id){
        return repository.findById(id)
                .map(jogo -> JogoResponse.fromDomain(jogo, avaliacaoJogoConsultaRepository.buscarEstatisticas(id)))
                .orElseThrow(()-> new BusinessException("Jogo não encontrado", HttpStatus.NOT_FOUND));
    }
}
