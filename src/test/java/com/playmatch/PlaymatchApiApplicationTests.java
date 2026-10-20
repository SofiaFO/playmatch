package com.playmatch;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Sobe o contexto completo e por isso precisa de um Neo4j acessível.
 * Só executa quando a configuração existe (variável NEO4J_URI ou arquivo .env).
 */
@SpringBootTest
@EnabledIf("ambienteConfigurado")
class PlaymatchApiApplicationTests {

	static boolean ambienteConfigurado() {
		return System.getenv("NEO4J_URI") != null || Files.exists(Path.of(".env"));
	}

	@Test
	void contextLoads() {
	}

}
