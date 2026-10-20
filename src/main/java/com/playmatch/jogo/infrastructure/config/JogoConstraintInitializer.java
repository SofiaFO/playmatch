package com.playmatch.jogo.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * The Component JogoConstraintInitializer
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
@Component
@RequiredArgsConstructor
public class JogoConstraintInitializer {

    private static final List<String> CONSTRAINTS = List.of(
            """
            CREATE CONSTRAINT jogo_id_unico IF NOT EXISTS
            FOR (j:Jogo) REQUIRE j.id IS UNIQUE
            """,
            """
            CREATE CONSTRAINT genero_nome_unico IF NOT EXISTS
            FOR (g:Genero) REQUIRE g.nome IS UNIQUE
            """,
            """
            CREATE CONSTRAINT plataforma_nome_unico IF NOT EXISTS
            FOR (p:Plataforma) REQUIRE p.nome IS UNIQUE
            """
    );

    private final Neo4jClient neo4jClient;

    @EventListener(ApplicationReadyEvent.class)
    public void criarConstraints() {
        CONSTRAINTS.forEach(constraint -> neo4jClient.query(constraint).run());
    }
}
