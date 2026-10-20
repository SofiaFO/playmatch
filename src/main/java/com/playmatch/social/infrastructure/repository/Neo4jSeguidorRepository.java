package com.playmatch.social.infrastructure.repository;


import com.playmatch.social.domain.UsuarioConectado;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.playmatch.shared.infrastructure.neo4j.Neo4jValores.deslocamento;
import static com.playmatch.shared.infrastructure.neo4j.Neo4jValores.instante;
import static com.playmatch.shared.infrastructure.neo4j.Neo4jValores.texto;
import static com.playmatch.shared.infrastructure.neo4j.Neo4jValores.uuid;

/**
 * The Repository Adapter Neo4jSeguidorRepository
 *
 * A relação SEGUE é mantida só via Cypher, e não mapeada na entidade Usuario:
 * mapeá-la faria o SDN carregar a rede de seguidores inteira a cada busca de usuário.
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
@Repository
@RequiredArgsConstructor
public class Neo4jSeguidorRepository implements SeguidorRepository {

    private static final String SEGUIR = """
            MATCH (seguidor:Usuario {id: $seguidorId}), (seguido:Usuario {id: $seguidoId})
            MERGE (seguidor)-[s:SEGUE]->(seguido)
            ON CREATE SET s.desde = datetime()
            """;

    private static final String DEIXAR_DE_SEGUIR = """
            MATCH (:Usuario {id: $seguidorId})-[s:SEGUE]->(:Usuario {id: $seguidoId})
            DELETE s
            RETURN count(*) AS removidas
            """;

    private static final String SEGUIDORES = """
            MATCH (outro:Usuario)-[s:SEGUE]->(:Usuario {id: $usuarioId})
            RETURN outro.id AS usuarioId, outro.nome AS nome, s.desde AS desde
            ORDER BY s.desde DESC, outro.nome ASC
            SKIP $deslocamento
            LIMIT $tamanho
            """;

    private static final String SEGUINDO = """
            MATCH (:Usuario {id: $usuarioId})-[s:SEGUE]->(outro:Usuario)
            RETURN outro.id AS usuarioId, outro.nome AS nome, s.desde AS desde
            ORDER BY s.desde DESC, outro.nome ASC
            SKIP $deslocamento
            LIMIT $tamanho
            """;

    private static final String CONTAR_SEGUIDORES = """
            MATCH (:Usuario)-[s:SEGUE]->(:Usuario {id: $usuarioId})
            RETURN count(s)
            """;

    private static final String CONTAR_SEGUINDO = """
            MATCH (:Usuario {id: $usuarioId})-[s:SEGUE]->(:Usuario)
            RETURN count(s)
            """;

    private final Neo4jClient neo4jClient;

    @Override
    public void seguir(UUID seguidorId, UUID seguidoId) {
        neo4jClient.query(SEGUIR)
                .bindAll(Map.of("seguidorId", seguidorId.toString(), "seguidoId", seguidoId.toString()))
                .run();
    }

    @Override
    public boolean deixarDeSeguir(UUID seguidorId, UUID seguidoId) {
        return neo4jClient.query(DEIXAR_DE_SEGUIR)
                .bindAll(Map.of("seguidorId", seguidorId.toString(), "seguidoId", seguidoId.toString()))
                .fetchAs(Long.class)
                .one()
                .orElse(0L) > 0;
    }

    @Override
    public List<UsuarioConectado> listarSeguidores(UUID usuarioId, int pagina, int tamanho) {
        return listar(SEGUIDORES, usuarioId, pagina, tamanho);
    }

    @Override
    public List<UsuarioConectado> listarSeguindo(UUID usuarioId, int pagina, int tamanho) {
        return listar(SEGUINDO, usuarioId, pagina, tamanho);
    }

    @Override
    public long contarSeguidores(UUID usuarioId) {
        return contar(CONTAR_SEGUIDORES, usuarioId);
    }

    @Override
    public long contarSeguindo(UUID usuarioId) {
        return contar(CONTAR_SEGUINDO, usuarioId);
    }

    private List<UsuarioConectado> listar(String cypher, UUID usuarioId, int pagina, int tamanho) {
        return neo4jClient.query(cypher)
                .bindAll(Map.of(
                        "usuarioId", usuarioId.toString(),
                        "deslocamento", deslocamento(pagina, tamanho),
                        "tamanho", tamanho
                ))
                .fetchAs(UsuarioConectado.class)
                .mappedBy((tipos, registro) -> new UsuarioConectado(
                        uuid(registro.get("usuarioId")),
                        texto(registro.get("nome")),
                        instante(registro.get("desde"))
                ))
                .all()
                .stream()
                .toList();
    }

    private long contar(String cypher, UUID usuarioId) {
        return neo4jClient.query(cypher)
                .bind(usuarioId.toString()).to("usuarioId")
                .fetchAs(Long.class)
                .one()
                .orElse(0L);
    }
}
