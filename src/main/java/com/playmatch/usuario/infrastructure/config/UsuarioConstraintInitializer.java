package com.playmatch.usuario.infrastructure.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Component;

/**
 * The Component UsuarioConstraintInitializer
 *
 * @author Sofia Ferreira de Oliveira
 * @since 24/09/2026
 */
@Component
@RequiredArgsConstructor
public class UsuarioConstraintInitializer {

    private final Neo4jClient neo4jClient;

    @EventListener(ApplicationReadyEvent.class)
    public void criarConstraintDeEmailUnico() {
        neo4jClient.query("""
                        CREATE CONSTRAINT usuario_email_unico IF NOT EXISTS
                        FOR (u:Usuario) REQUIRE u.email IS UNIQUE
                        """)
                .run();
    }
}
