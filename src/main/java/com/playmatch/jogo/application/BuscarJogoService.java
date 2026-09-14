package com.playmatch.jogo.application;


import com.playmatch.shared.exception.BusinessException;
import com.playmatch.jogo.infrastructure.dto.JogoResponse;
import com.playmatch.jogo.infrastructure.repository.JogoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * The Domain Class BuscarJogo
 *
 * @author Sofia Ferreira de Oliveira
 * @since 27/08/2026
 */

@Service
@RequiredArgsConstructor
public class BuscarJogoService {

    private final JogoRepository repository;

    public JogoResponse executar(Long id){
        return repository.findById(id)
                .map(JogoResponse::fromDomain)
                .orElseThrow(()-> new BusinessException("Jogo não encontrado", HttpStatus.NOT_FOUND));
    }
}
