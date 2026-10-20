package com.playmatch.social.application;


import com.playmatch.shared.application.security.UsuarioAutenticado;
import com.playmatch.social.domain.Atividade;
import com.playmatch.social.infrastructure.dto.AtividadeResponse;
import com.playmatch.social.infrastructure.dto.FeedResponse;
import com.playmatch.social.infrastructure.repository.FeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.playmatch.shared.infrastructure.neo4j.Neo4jValores.deslocamento;

/**
 * The Service Class BuscarFeedService
 *
 * Busca um item a mais do que o tamanho da página só para saber se existe
 * uma próxima página, sem precisar contar o feed inteiro.
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */

@Service
@RequiredArgsConstructor
public class BuscarFeedService {

    private final FeedRepository feedRepository;
    private final UsuarioAutenticado usuarioAutenticado;

    public FeedResponse executar(int pagina, int tamanho) {
        List<Atividade> atividades = feedRepository.buscarFeed(
                usuarioAutenticado.id(),
                deslocamento(pagina, tamanho),
                tamanho + 1
        );

        boolean temMais = atividades.size() > tamanho;

        return new FeedResponse(
                atividades.stream().limit(tamanho).map(AtividadeResponse::fromDomain).toList(),
                pagina,
                tamanho,
                temMais
        );
    }
}
