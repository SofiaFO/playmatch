package com.playmatch.social.infrastructure.repository;


import com.playmatch.social.domain.Atividade;
import com.playmatch.social.domain.TipoAtividade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.playmatch.shared.infrastructure.neo4j.Neo4jValores.instante;
import static com.playmatch.shared.infrastructure.neo4j.Neo4jValores.inteiro;
import static com.playmatch.shared.infrastructure.neo4j.Neo4jValores.longo;
import static com.playmatch.shared.infrastructure.neo4j.Neo4jValores.texto;
import static com.playmatch.shared.infrastructure.neo4j.Neo4jValores.uuid;

/**
 * The Repository Adapter Neo4jFeedRepository
 *
 * O feed é derivado das próprias relações do grafo (AVALIOU, FAVORITOU, QUER_JOGAR, SEGUE),
 * sem uma tabela de eventos separada. Relações gravadas antes de existirem datas
 * (avaliações antigas) não aparecem no feed.
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
@Repository
@RequiredArgsConstructor
public class Neo4jFeedRepository implements FeedRepository {

    private static final String FEED = """
            MATCH (:Usuario {id: $usuarioId})-[:SEGUE]->(autor:Usuario)
            CALL {
                WITH autor
                MATCH (autor)-[a:AVALIOU]->(j:Jogo)
                RETURN 'AVALIOU' AS tipo, j AS jogo, a.nota AS nota, a.resenha AS resenha,
                       null AS seguido, coalesce(a.atualizadaEm, a.criadaEm) AS data
                UNION ALL
                WITH autor
                MATCH (autor)-[f:FAVORITOU]->(j:Jogo)
                RETURN 'FAVORITOU' AS tipo, j AS jogo, null AS nota, null AS resenha,
                       null AS seguido, f.adicionadoEm AS data
                UNION ALL
                WITH autor
                MATCH (autor)-[d:QUER_JOGAR]->(j:Jogo)
                RETURN 'QUER_JOGAR' AS tipo, j AS jogo, null AS nota, null AS resenha,
                       null AS seguido, d.adicionadoEm AS data
                UNION ALL
                WITH autor
                MATCH (autor)-[s:SEGUE]->(outro:Usuario)
                RETURN 'COMECOU_A_SEGUIR' AS tipo, null AS jogo, null AS nota, null AS resenha,
                       outro AS seguido, s.desde AS data
            }
            WITH autor, tipo, jogo, nota, resenha, seguido, data
            WHERE data IS NOT NULL
            RETURN tipo, autor.id AS autorId, autor.nome AS autorNome,
                   jogo.id AS jogoId, jogo.nome AS jogoNome, jogo.imagemUrl AS jogoImagemUrl,
                   nota, resenha, seguido.id AS seguidoId, seguido.nome AS seguidoNome, data
            ORDER BY data DESC
            SKIP $deslocamento
            LIMIT $limite
            """;

    private final Neo4jClient neo4jClient;

    @Override
    public List<Atividade> buscarFeed(UUID usuarioId, long deslocamento, int limite) {
        return neo4jClient.query(FEED)
                .bindAll(Map.of(
                        "usuarioId", usuarioId.toString(),
                        "deslocamento", deslocamento,
                        "limite", limite
                ))
                .fetchAs(Atividade.class)
                .mappedBy((tipos, registro) -> new Atividade(
                        TipoAtividade.valueOf(registro.get("tipo").asString()),
                        uuid(registro.get("autorId")),
                        texto(registro.get("autorNome")),
                        longo(registro.get("jogoId")),
                        texto(registro.get("jogoNome")),
                        texto(registro.get("jogoImagemUrl")),
                        inteiro(registro.get("nota")),
                        texto(registro.get("resenha")),
                        uuid(registro.get("seguidoId")),
                        texto(registro.get("seguidoNome")),
                        instante(registro.get("data"))
                ))
                .all()
                .stream()
                .toList();
    }
}
