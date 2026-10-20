package com.playmatch;

import com.jayway.jsonpath.JsonPath;
import com.playmatch.jogo.domain.Jogo;
import com.playmatch.jogo.infrastructure.repository.JogoRepository;
import com.playmatch.support.Neo4jIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Sobe a aplicação inteira (segurança, controllers, constraints do Neo4j) contra o
 * Neo4j do Testcontainers e percorre o fluxo principal com tokens JWT reais.
 * A RAWG não é chamada: o jogo usado já é gravado direto no catálogo.
 */
@SpringBootTest(properties = {
        "rawg.api-key=chave-de-teste",
        "jwt.secret=segredo-de-teste-com-pelo-menos-32-bytes!!",
        "jwt.expiration-ms=600000"
})
@AutoConfigureMockMvc
@EnabledIf("com.playmatch.support.Neo4jIntegrationTest#dockerDisponivel")
class PlaymatchApiApplicationTests extends Neo4jIntegrationTest {

	private static final String SENHA = "Senha@123";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JogoRepository jogoRepository;

	@Autowired
	private Neo4jClient neo4jClient;

	@BeforeEach
	void limparBanco() {
		neo4jClient.query("MATCH (n) DETACH DELETE n").run();
		jogoRepository.save(Jogo.importar(
				1L, "Hades", "Roguelike", LocalDate.of(2020, 9, 17), null, 4.6,
				List.of("Action", "Roguelike"), List.of("PC")
		));
	}

	@Test
	void rotasProtegidasExigemToken() throws Exception {
		mockMvc.perform(get("/feed"))
				.andExpect(status().isUnauthorized())
				.andExpect(jsonPath("$.mensagem").value("Token de autenticação ausente ou inválido"));
	}

	@Test
	void fluxoSocialCompleto() throws Exception {
		String ana = cadastrar("Ana", "ana@playmatch.com");
		String bruno = cadastrar("Bruno", "bruno@playmatch.com");
		String tokenAna = login("ana@playmatch.com");
		String tokenBruno = login("bruno@playmatch.com");

		// Bruno avalia com resenha; Ana não consegue avaliar em nome dele
		mockMvc.perform(post("/avaliacoes").header("Authorization", tokenBruno)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"usuarioId": "%s", "jogoId": 1, "nota": 5, "resenha": "Obra-prima"}
								""".formatted(bruno)))
				.andExpect(status().isNoContent());
		mockMvc.perform(post("/avaliacoes").header("Authorization", tokenAna)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"usuarioId": "%s", "jogoId": 1, "nota": 1}
								""".formatted(bruno)))
				.andExpect(status().isForbidden());

		// Média do PlayMatch e avaliações do jogo
		mockMvc.perform(get("/jogos/1").header("Authorization", tokenAna))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.mediaPlaymatch").value(5.0))
				.andExpect(jsonPath("$.totalAvaliacoes").value(1));
		mockMvc.perform(get("/jogos/1/avaliacoes").param("comResenha", "true").header("Authorization", tokenAna))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalItens").value(1))
				.andExpect(jsonPath("$.itens[0].resenha").value("Obra-prima"));

		// Ana segue Bruno e vê a atividade dele no feed
		mockMvc.perform(post("/usuarios/{id}/seguir", bruno).header("Authorization", tokenAna))
				.andExpect(status().isNoContent());
		mockMvc.perform(post("/usuarios/{id}/seguir", ana).header("Authorization", tokenAna))
				.andExpect(status().isUnprocessableContent());
		mockMvc.perform(get("/feed").header("Authorization", tokenAna))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.itens[0].descricao").value("Bruno avaliou Hades com nota 5"))
				.andExpect(jsonPath("$.itens[0].jogo.id").value(1))
				.andExpect(jsonPath("$.temMais").value(false));

		// Lista de desejos
		mockMvc.perform(post("/usuarios/{id}/desejos/1", ana).header("Authorization", tokenAna))
				.andExpect(status().isNoContent());
		mockMvc.perform(post("/usuarios/{id}/desejos/1", ana).header("Authorization", tokenAna))
				.andExpect(status().isConflict());
		mockMvc.perform(get("/usuarios/{id}/desejos", ana).header("Authorization", tokenBruno))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].nome").value("Hades"));

		// Perfis
		mockMvc.perform(get("/usuarios/me/perfil").header("Authorization", tokenAna))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.ehVoce").value(true))
				.andExpect(jsonPath("$.seguindo").value(1))
				.andExpect(jsonPath("$.totalDesejos").value(1));
		mockMvc.perform(get("/usuarios/{id}/perfil", bruno).header("Authorization", tokenAna))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.voceSegue").value(true))
				.andExpect(jsonPath("$.seguidores").value(1))
				.andExpect(jsonPath("$.generosFavoritos[0]").value("Action"))
				.andExpect(jsonPath("$.ultimasAvaliacoes[0].resenha").value("Obra-prima"));
	}

	private String cadastrar(String nome, String email) throws Exception {
		String resposta = mockMvc.perform(post("/usuarios")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"nome": "%s", "email": "%s", "senha": "%s"}
								""".formatted(nome, email, SENHA)))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();

		return JsonPath.read(resposta, "$.id");
	}

	private String login(String email) throws Exception {
		String resposta = mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"email": "%s", "senha": "%s"}
								""".formatted(email, SENHA)))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		return "Bearer " + JsonPath.read(resposta, "$.token");
	}
}
