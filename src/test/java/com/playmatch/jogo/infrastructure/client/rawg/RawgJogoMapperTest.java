package com.playmatch.jogo.infrastructure.client.rawg;

import com.playmatch.jogo.infrastructure.client.rawg.dto.RawgJogoDetalhesResponse;
import com.playmatch.jogo.infrastructure.client.rawg.dto.RawgPesquisaResponse;
import com.playmatch.jogo.infrastructure.dto.JogoDetalheResponse;
import com.playmatch.jogo.infrastructure.dto.JogoPesquisaResponse;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

class RawgJogoMapperTest {

    private final JsonMapper jsonMapper = JsonMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    @Test
    void pesquisaMapeiaPlataformasNoFormatoDaRawg() {
        String json = """
                {
                  "count": 1,
                  "results": [{
                    "id": 3498,
                    "name": "Grand Theft Auto V",
                    "released": "2013-09-17",
                    "background_image": "https://img/gta.jpg",
                    "rating": 4.47,
                    "genres": [{"id": 4, "name": "Action"}, {"id": 3, "name": "Adventure"}],
                    "platforms": [
                      {"platform": {"id": 4, "name": "PC"}, "released_at": "2013-09-17"},
                      {"platform": {"id": 187, "name": "PlayStation 5"}}
                    ]
                  }]
                }
                """;

        RawgPesquisaResponse rawg = jsonMapper.readValue(json, RawgPesquisaResponse.class);
        JogoPesquisaResponse jogo = RawgJogoMapper.toPesquisaResponse(rawg.results().getFirst());

        assertThat(jogo.nome()).isEqualTo("Grand Theft Auto V");
        assertThat(jogo.generos()).containsExactly("Action", "Adventure");
        assertThat(jogo.plataformas()).containsExactly("PC", "PlayStation 5");
    }

    @Test
    void detalhesSemGenerosNemPlataformasNaoQuebra() {
        String json = """
                {"id": 1, "name": "Jogo sem dados", "genres": null}
                """;

        RawgJogoDetalhesResponse rawg = jsonMapper.readValue(json, RawgJogoDetalhesResponse.class);
        JogoDetalheResponse jogo = RawgJogoMapper.toDetalheResponse(rawg);

        assertThat(jogo.generos()).isEmpty();
        assertThat(jogo.plataformas()).isEmpty();
    }

    @Test
    void pesquisaSemResultadosRetornaListaVazia() {
        RawgPesquisaResponse rawg = jsonMapper.readValue("{\"count\": 0}", RawgPesquisaResponse.class);

        assertThat(rawg.results()).isEmpty();
    }
}
