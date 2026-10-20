package com.playmatch.jogo.application;


import com.playmatch.jogo.domain.EstatisticasAvaliacao;
import com.playmatch.jogo.domain.Jogo;
import com.playmatch.jogo.infrastructure.dto.JogoResponse;
import com.playmatch.jogo.infrastructure.repository.AvaliacaoJogoConsultaRepository;
import com.playmatch.jogo.infrastructure.repository.JogoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

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
    private final AvaliacaoJogoConsultaRepository avaliacaoJogoConsultaRepository;

    public List<JogoResponse> executar() {
        List<Jogo> jogos = repository.findAll();

        Map<Long, EstatisticasAvaliacao> estatisticas = avaliacaoJogoConsultaRepository.buscarEstatisticas(
                jogos.stream().map(Jogo::getId).toList()
        );

        return jogos.stream()
                .map(jogo -> JogoResponse.fromDomain(
                        jogo,
                        estatisticas.getOrDefault(jogo.getId(), EstatisticasAvaliacao.vazia())
                ))
                .sorted(Comparator.comparing(JogoResponse::nome))
                .toList();
    }
}
