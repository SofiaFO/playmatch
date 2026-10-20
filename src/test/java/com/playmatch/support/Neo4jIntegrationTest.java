package com.playmatch.support;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.neo4j.Neo4jContainer;

/**
 * Base dos testes que rodam contra um Neo4j real.
 *
 * Um único container é iniciado na primeira classe que precisar dele e reaproveitado
 * pelas demais (o Testcontainers o encerra ao final da JVM). As subclasses devem usar
 * {@code @EnabledIf("com.playmatch.support.Neo4jIntegrationTest#dockerDisponivel")}
 * para serem puladas em máquinas sem Docker.
 */
public abstract class Neo4jIntegrationTest {

    private static Neo4jContainer neo4j;

    public static boolean dockerDisponivel() {
        return DockerClientFactory.instance().isDockerAvailable();
    }

    @DynamicPropertySource
    static void neo4jProperties(DynamicPropertyRegistry registry) {
        Neo4jContainer container = container();

        registry.add("spring.neo4j.uri", container::getBoltUrl);
        registry.add("spring.neo4j.authentication.username", () -> "neo4j");
        registry.add("spring.neo4j.authentication.password", container::getAdminPassword);
    }

    private static synchronized Neo4jContainer container() {
        if (neo4j == null) {
            neo4j = new Neo4jContainer("neo4j:5");
            neo4j.start();
        }

        return neo4j;
    }
}
