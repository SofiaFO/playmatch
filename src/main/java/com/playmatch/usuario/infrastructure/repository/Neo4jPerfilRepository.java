package com.playmatch.usuario.infrastructure.repository;


import com.playmatch.usuario.domain.EstatisticasPerfil;
import lombok.RequiredArgsConstructor;
import org.neo4j.driver.Value;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.UUID;

import static com.playmatch.shared.infrastructure.neo4j.Neo4jValores.decimal;

/**
 * The Repository Adapter Neo4jPerfilRepository
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
@Repository
@RequiredArgsConstructor
public class Neo4jPerfilRepository implements PerfilRepository {

    static final int NOTA_MINIMA_GOSTO = 4;

    private static final String ESTATISTICAS = """
            MATCH (u:Usuario {id: $usuarioId})
            CALL {
                WITH u
                OPTIONAL MATCH (u)-[a:AVALIOU]->(:Jogo)
                RETURN count(a) AS totalAvaliacoes, avg(a.nota) AS notaMedia
            }
            CALL {
                WITH u
                OPTIONAL MATCH (u)-[f:FAVORITOU]->(:Jogo)
                RETURN count(f) AS totalFavoritos
            }
            CALL {
                WITH u
                OPTIONAL MATCH (u)-[d:QUER_JOGAR]->(:Jogo)
                RETURN count(d) AS totalDesejos
            }
            CALL {
                WITH u
                OPTIONAL MATCH (s:Usuario)-[:SEGUE]->(u)
                RETURN count(s) AS seguidores
            }
            CALL {
                WITH u
                OPTIONAL MATCH (u)-[:SEGUE]->(s:Usuario)
                RETURN count(s) AS seguindo
            }
            CALL {
                WITH u
                MATCH (u)-[r:AVALIOU|FAVORITOU]->(j:Jogo)-[:TEM_GENERO]->(g:Genero)
                WHERE type(r) = 'FAVORITOU' OR r.nota >= $notaMinima
                WITH g, count(DISTINCT j) AS jogos
                ORDER BY jogos DESC, g.nome ASC
                LIMIT 3
                RETURN collect(g.nome) AS generosFavoritos
            }
            CALL {
                WITH u
                MATCH (u)-[r:AVALIOU|FAVORITOU]->(j:Jogo)-[:DISPONIVEL_EM]->(p:Plataforma)
                WHERE type(r) = 'FAVORITOU' OR r.nota >= $notaMinima
                WITH p, count(DISTINCT j) AS jogos
                ORDER BY jogos DESC, p.nome ASC
                LIMIT 3
                RETURN collect(p.nome) AS plataformasFavoritas
            }
            RETURN totalAvaliacoes, notaMedia, totalFavoritos, totalDesejos, seguidores, seguindo,
                   generosFavoritos, plataformasFavoritas,
                   EXISTS { MATCH (:Usuario {id: $visitanteId})-[:SEGUE]->(u) } AS visitanteSegue
            """;

    private final Neo4jClient neo4jClient;

    @Override
    public EstatisticasPerfil buscarEstatisticas(UUID usuarioId, UUID visitanteId) {
        return neo4jClient.query(ESTATISTICAS)
                .bindAll(Map.of(
                        "usuarioId", usuarioId.toString(),
                        "visitanteId", visitanteId.toString(),
                        "notaMinima", NOTA_MINIMA_GOSTO
                ))
                .fetchAs(EstatisticasPerfil.class)
                .mappedBy((tipos, registro) -> {
                    Double media = decimal(registro.get("notaMedia"));

                    return new EstatisticasPerfil(
                            registro.get("totalAvaliacoes").asLong(),
                            media == null ? null : Math.round(media * 10) / 10.0,
                            registro.get("totalFavoritos").asLong(),
                            registro.get("totalDesejos").asLong(),
                            registro.get("seguidores").asLong(),
                            registro.get("seguindo").asLong(),
                            registro.get("generosFavoritos").asList(Value::asString),
                            registro.get("plataformasFavoritas").asList(Value::asString),
                            registro.get("visitanteSegue").asBoolean()
                    );
                })
                .one()
                .orElseThrow();
    }
}
