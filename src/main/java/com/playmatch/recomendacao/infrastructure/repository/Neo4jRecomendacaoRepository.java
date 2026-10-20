package com.playmatch.recomendacao.infrastructure.repository;


import com.playmatch.recomendacao.domain.JogoRecomendado;
import com.playmatch.recomendacao.domain.UsuarioCompativel;
import lombok.RequiredArgsConstructor;
import org.neo4j.driver.Record;
import org.neo4j.driver.Value;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * The Repository Adapter Neo4jRecomendacaoRepository
 *
 * Gênero pesa mais do que plataforma na similaridade entre jogos, porque diz
 * mais sobre o gosto do jogador do que o aparelho em que o jogo roda.
 * Favoritar um jogo conta como dar nota 5, e jogos que o usuário já conhece
 * (avaliados, favoritos ou na lista de desejos) nunca são recomendados.
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
@Repository
@RequiredArgsConstructor
public class Neo4jRecomendacaoRepository implements RecomendacaoRepository {

    private static final String POR_PERFIL = """
            MATCH (u:Usuario {id: $usuarioId})-[r:AVALIOU|FAVORITOU]->(curtido:Jogo)
            WITH u, curtido, max(CASE WHEN type(r) = 'FAVORITOU' THEN 5 ELSE r.nota END) AS nota
            WHERE nota >= $notaMinima
            MATCH (curtido)-[:TEM_GENERO|DISPONIVEL_EM]->(c)<-[:TEM_GENERO|DISPONIVEL_EM]-(rec:Jogo)
            WHERE NOT EXISTS { (u)-[:AVALIOU|FAVORITOU|QUER_JOGAR]->(rec) }
            WITH rec,
                 sum(CASE WHEN c:Genero THEN 2.0 ELSE 1.0 END * (nota - $notaMinima + 1)) AS pontuacao,
                 collect(DISTINCT CASE WHEN c:Genero THEN c.nome END) AS generos
            RETURN rec.id AS jogoId, rec.nome AS nome, rec.imagemUrl AS imagemUrl,
                   rec.notaRawg AS notaRawg, pontuacao, generos
            ORDER BY pontuacao DESC, rec.notaRawg DESC
            LIMIT $limite
            """;

    private static final String POR_JOGADORES_PARECIDOS = """
            MATCH (u:Usuario {id: $usuarioId})-[a1:AVALIOU]->(:Jogo)<-[a2:AVALIOU]-(outro:Usuario)
            WHERE outro <> u
            WITH u, outro, count(*) AS emComum, avg(abs(a1.nota - a2.nota)) AS diferenca
            WHERE diferenca <= 1.0
            MATCH (outro)-[a3:AVALIOU]->(rec:Jogo)
            WHERE a3.nota >= $notaMinima AND NOT EXISTS { (u)-[:AVALIOU|FAVORITOU|QUER_JOGAR]->(rec) }
            WITH rec,
                 sum(a3.nota * emComum * (1.0 - diferenca / 4.0)) AS pontuacao,
                 count(DISTINCT outro) AS jogadores
            RETURN rec.id AS jogoId, rec.nome AS nome, rec.imagemUrl AS imagemUrl,
                   rec.notaRawg AS notaRawg, pontuacao, jogadores
            ORDER BY pontuacao DESC, rec.notaRawg DESC
            LIMIT $limite
            """;

    // Média bayesiana: a nota da RAWG funciona como "2 avaliações virtuais",
    // para que um jogo com uma única nota 5 não passe na frente de um consagrado.
    private static final String POPULARES = """
            MATCH (rec:Jogo)
            WHERE NOT EXISTS { (:Usuario {id: $usuarioId})-[:AVALIOU|FAVORITOU|QUER_JOGAR]->(rec) }
            OPTIONAL MATCH (:Usuario)-[a:AVALIOU]->(rec)
            WITH rec, count(a) AS avaliacoes, coalesce(sum(a.nota), 0) AS soma
            WITH rec, (soma + 2 * coalesce(rec.notaRawg, 3.0)) / (avaliacoes + 2.0) AS pontuacao
            RETURN rec.id AS jogoId, rec.nome AS nome, rec.imagemUrl AS imagemUrl,
                   rec.notaRawg AS notaRawg, pontuacao
            ORDER BY pontuacao DESC, rec.nome ASC
            LIMIT $limite
            """;

    private static final String SIMILARES = """
            MATCH (ref:Jogo {id: $jogoId})-[:TEM_GENERO|DISPONIVEL_EM]->(c)<-[:TEM_GENERO|DISPONIVEL_EM]-(rec:Jogo)
            WHERE rec <> ref
            WITH rec,
                 sum(CASE WHEN c:Genero THEN 2.0 ELSE 1.0 END) AS pontuacao,
                 collect(DISTINCT CASE WHEN c:Genero THEN c.nome END) AS generos
            RETURN rec.id AS jogoId, rec.nome AS nome, rec.imagemUrl AS imagemUrl,
                   rec.notaRawg AS notaRawg, pontuacao, generos
            ORDER BY pontuacao DESC, rec.notaRawg DESC
            LIMIT $limite
            """;

    // A confiança (n / (n + 2)) evita que alguém com 1 jogo em comum e a mesma nota
    // fique acima de quem tem 10 jogos em comum com notas quase iguais.
    private static final String USUARIOS_COMPATIVEIS = """
            MATCH (u:Usuario {id: $usuarioId})-[a1:AVALIOU]->(j:Jogo)<-[a2:AVALIOU]-(outro:Usuario)
            WHERE outro <> u
            WITH outro, count(j) AS jogosEmComum,
                 avg(abs(a1.nota - a2.nota)) AS diferencaMedia,
                 collect(j.nome) AS jogos
            RETURN outro.id AS usuarioId, outro.nome AS nome, jogosEmComum, diferencaMedia,
                   jogos[..3] AS exemplos
            ORDER BY (1.0 - diferencaMedia / 4.0) * jogosEmComum / (jogosEmComum + 2.0) DESC, nome ASC
            LIMIT $limite
            """;

    private final Neo4jClient neo4jClient;

    @Override
    public List<JogoRecomendado> recomendarPorPerfil(UUID usuarioId, int notaMinima, int limite) {
        return buscarJogos(
                POR_PERFIL,
                Map.of("usuarioId", usuarioId.toString(), "notaMinima", notaMinima, "limite", limite),
                registro -> JogoRecomendado.porGeneros(
                        registro.get("jogoId").asLong(),
                        texto(registro.get("nome")),
                        texto(registro.get("imagemUrl")),
                        decimal(registro.get("notaRawg")),
                        registro.get("pontuacao").asDouble(),
                        registro.get("generos").asList(Value::asString)
                )
        );
    }

    @Override
    public List<JogoRecomendado> recomendarPorJogadoresParecidos(UUID usuarioId, int notaMinima, int limite) {
        return buscarJogos(
                POR_JOGADORES_PARECIDOS,
                Map.of("usuarioId", usuarioId.toString(), "notaMinima", notaMinima, "limite", limite),
                registro -> JogoRecomendado.porJogadoresParecidos(
                        registro.get("jogoId").asLong(),
                        texto(registro.get("nome")),
                        texto(registro.get("imagemUrl")),
                        decimal(registro.get("notaRawg")),
                        registro.get("pontuacao").asDouble(),
                        registro.get("jogadores").asLong()
                )
        );
    }

    @Override
    public List<JogoRecomendado> buscarPopulares(UUID usuarioId, int limite) {
        return buscarJogos(
                POPULARES,
                Map.of("usuarioId", usuarioId.toString(), "limite", limite),
                registro -> JogoRecomendado.popular(
                        registro.get("jogoId").asLong(),
                        texto(registro.get("nome")),
                        texto(registro.get("imagemUrl")),
                        decimal(registro.get("notaRawg")),
                        registro.get("pontuacao").asDouble()
                )
        );
    }

    @Override
    public List<JogoRecomendado> buscarSimilares(Long jogoId, int limite) {
        return buscarJogos(
                SIMILARES,
                Map.of("jogoId", jogoId, "limite", limite),
                registro -> JogoRecomendado.porSimilaridade(
                        registro.get("jogoId").asLong(),
                        texto(registro.get("nome")),
                        texto(registro.get("imagemUrl")),
                        decimal(registro.get("notaRawg")),
                        registro.get("pontuacao").asDouble(),
                        registro.get("generos").asList(Value::asString)
                )
        );
    }

    @Override
    public List<UsuarioCompativel> buscarUsuariosCompativeis(UUID usuarioId, int limite) {
        return neo4jClient.query(USUARIOS_COMPATIVEIS)
                .bindAll(Map.of("usuarioId", usuarioId.toString(), "limite", limite))
                .fetchAs(UsuarioCompativel.class)
                .mappedBy((tipos, registro) -> new UsuarioCompativel(
                        UUID.fromString(registro.get("usuarioId").asString()),
                        texto(registro.get("nome")),
                        registro.get("jogosEmComum").asLong(),
                        UsuarioCompativel.calcularCompatibilidade(registro.get("diferencaMedia").asDouble()),
                        registro.get("exemplos").asList(Value::asString)
                ))
                .all()
                .stream()
                .toList();
    }

    private List<JogoRecomendado> buscarJogos(
            String cypher,
            Map<String, Object> parametros,
            Function<Record, JogoRecomendado> mapeador
    ) {
        return neo4jClient.query(cypher)
                .bindAll(parametros)
                .fetchAs(JogoRecomendado.class)
                .mappedBy((tipos, registro) -> mapeador.apply(registro))
                .all()
                .stream()
                .toList();
    }

    private static String texto(Value valor) {
        return valor.isNull() ? null : valor.asString();
    }

    private static Double decimal(Value valor) {
        return valor.isNull() ? null : valor.asDouble();
    }
}
