package com.playmatch.jogo.application;


import com.playmatch.jogo.domain.Jogo;
import com.playmatch.jogo.infrastructure.client.rawg.RawgClient;
import com.playmatch.jogo.infrastructure.client.rawg.RawgJogoMapper;
import com.playmatch.jogo.infrastructure.repository.JogoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Service Class ImportarJogoService
 *
 * Traz um jogo da RAWG para o catálogo local. Se o jogo já foi importado,
 * devolve o registro existente sem consultar a RAWG novamente.
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */

@Service
@RequiredArgsConstructor
public class ImportarJogoService {

    private final JogoRepository jogoRepository;
    private final RawgClient rawgClient;

    @Transactional
    public ResultadoImportacao executar(Long rawgId) {
        return jogoRepository.findById(rawgId)
                .map(jogo -> new ResultadoImportacao(jogo, false))
                .orElseGet(() -> new ResultadoImportacao(importar(rawgId), true));
    }

    @Transactional
    public Jogo obterOuImportar(Long rawgId) {
        return executar(rawgId).jogo();
    }

    private Jogo importar(Long rawgId) {
        Jogo jogo = RawgJogoMapper.toDomain(rawgClient.buscarPorId(rawgId));

        return jogoRepository.save(jogo);
    }
}
