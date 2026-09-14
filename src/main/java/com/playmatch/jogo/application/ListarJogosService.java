package com.playmatch.jogo.application;


import com.playmatch.jogo.domain.Genero;
import com.playmatch.jogo.infrastructure.dto.JogoResponse;
import com.playmatch.jogo.infrastructure.repository.JogoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * The Domain Class ListarJogosService
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
                .map(jogo -> new JogoResponse(
                        jogo.getId(),
                        jogo.getNome(),
                        jogo.getAno(),
                        jogo.getGeneros()
                                .stream()
                                .map(Genero::getNome)
                                .sorted()
                                .toList()
                ))
                .sorted(Comparator.comparing(JogoResponse::nome))
                .toList();
    }
}
