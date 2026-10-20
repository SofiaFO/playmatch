package com.playmatch.shared.infrastructure.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PaginaResponseTest {

    @Test
    void calculaTotalDePaginasArredondandoParaCima() {
        PaginaResponse<String> pagina = PaginaResponse.de(List.of(1, 2), 0, 2, 5, String::valueOf);

        assertThat(pagina.itens()).containsExactly("1", "2");
        assertThat(pagina.totalPaginas()).isEqualTo(3);
    }

    @Test
    void semItensNaoTemPaginas() {
        assertThat(PaginaResponse.de(List.<Integer>of(), 0, 20, 0, String::valueOf).totalPaginas()).isZero();
    }
}
