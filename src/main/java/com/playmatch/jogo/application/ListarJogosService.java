package com.playmatch.jogo.application;


import com.playmatch.jogo.infrastructure.dto.JogoResponse;
import com.playmatch.jogo.infrastructure.repository.JogoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * The Service Class ListarJogosService
 *
 * @author Sofia Ferreira de Oliveira
 * @since 26/08/2026
 */

@Service
@RequiredArgsConstructor
public class ListarJogosService {

    private final JogoRepository repository;

    public List<JogoResponse> executar() {
        return repository.findAll().stream()
                .map(JogoResponse::fromDomain)
                .sorted(Comparator.comparing(JogoResponse::nome))
                .toList();
    }
}
