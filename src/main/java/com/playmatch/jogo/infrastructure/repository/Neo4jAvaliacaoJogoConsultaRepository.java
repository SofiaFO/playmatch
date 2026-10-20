package com.playmatch.jogo.infrastructure.repository;


import com.playmatch.jogo.domain.AvaliacaoDoJogo;
import com.playmatch.jogo.domain.EstatisticasAvaliacao;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.playmatch.shared.infrastructure.neo4j.Neo4jValores.decimal;
import static com.playmatch.shared.infrastructure.neo4j.Neo4jValores.deslocamento;
import static com.playmatch.shared.infrastructure.neo4j.Neo4jValores.instante;
import static com.playmatch.shared.infrastructure.neo4j.Neo4jValores.inteiro;
import static com.playmatch.shared.infrastructure.neo4j.Neo4jValores.texto;
import static com.playmatch.shared.infrastructure.neo4j.Neo4jValores.uuid;

/**
 * The Repository Adapter Neo4jAvaliacaoJogoConsultaRepository
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
@Repository
@RequiredArgsConstructor
public class Neo4jAvaliacaoJogoConsultaRepository implements AvaliacaoJogoConsultaRepository {

    private static final String ESTATISTICAS = """
            MATCH (j:Jogo) WHERE j.id IN $jogoIds
            MATCH (:Usuario)-[a:AVALIOU]->(j)
            RETURN j.id AS jogoId, avg(a.nota) AS media, count(a) AS total
            """;

    private static final String AVALIACOES = """
            MATCH (u:Usuario)-[a:AVALIOU]->(:Jogo {id: $jogoId})
            WHERE NOT $somenteComResenha OR a.resenha IS NOT NULL
            RETURN u.id AS usuarioId, u.nome AS nomeUsuario, a.nota AS nota, a.resenha AS resenha,
                   a.criadaEm AS criadaEm, a.atualizadaEm AS atualizadaEm
            ORDER BY coalesce(a.atualizadaEm, a.criadaEm) DESC, u.nome ASC
            SKIP $deslocamento
            LIMIT $tamanho
            """;

    private static final String CONTAGEM = """
            MATCH (:Usuario)-[a:AVALIOU]->(:Jogo {id: $jogoId})
            WHERE NOT $somenteComResenha OR a.resenha IS NOT NULL
            RETURN count(a)
            """;

    private final Neo4jClient neo4jClient;

    @Override
    public EstatisticasAvaliacao buscarEstatisticas(Long jogoId) {
        return buscarEstatisticas(List.of(jogoId))
                .getOrDefault(jogoId, EstatisticasAvaliacao.vazia());
    }

    @Override
    public Map<Long, EstatisticasAvaliacao> buscarEstatisticas(Collection<Long> jogoIds) {
        if (jogoIds.isEmpty()) {
            return Map.of();
        }

        return neo4jClient.query(ESTATISTICAS)
                .bind(List.copyOf(jogoIds)).to("jogoIds")
                .fetchAs(EstatisticaDoJogo.class)
                .mappedBy((tipos, registro) -> new EstatisticaDoJogo(
                        registro.get("jogoId").asLong(),
                        EstatisticasAvaliacao.de(decimal(registro.get("media")), registro.get("total").asLong())
                ))
                .all()
                .stream()
                .collect(Collectors.toMap(EstatisticaDoJogo::jogoId, EstatisticaDoJogo::estatisticas));
    }

    @Override
    public List<AvaliacaoDoJogo> listarAvaliacoes(Long jogoId, boolean somenteComResenha, int pagina, int tamanho) {
        return neo4jClient.query(AVALIACOES)
                .bindAll(Map.of(
                        "jogoId", jogoId,
                        "somenteComResenha", somenteComResenha,
                        "deslocamento", deslocamento(pagina, tamanho),
                        "tamanho", tamanho
                ))
                .fetchAs(AvaliacaoDoJogo.class)
                .mappedBy((tipos, registro) -> new AvaliacaoDoJogo(
                        uuid(registro.get("usuarioId")),
                        texto(registro.get("nomeUsuario")),
                        inteiro(registro.get("nota")),
                        texto(registro.get("resenha")),
                        instante(registro.get("criadaEm")),
                        instante(registro.get("atualizadaEm"))
                ))
                .all()
                .stream()
                .toList();
    }

    @Override
    public long contarAvaliacoes(Long jogoId, boolean somenteComResenha) {
        return neo4jClient.query(CONTAGEM)
                .bindAll(Map.of("jogoId", jogoId, "somenteComResenha", somenteComResenha))
                .fetchAs(Long.class)
                .one()
                .orElse(0L);
    }

    private record EstatisticaDoJogo(Long jogoId, EstatisticasAvaliacao estatisticas) {
    }
}
