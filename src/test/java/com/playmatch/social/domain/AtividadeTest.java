package com.playmatch.social.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AtividadeTest {

    private static Atividade atividade(TipoAtividade tipo) {
        return new Atividade(
                tipo, UUID.randomUUID(), "Ana", 1L, "Hades", null, 5, null,
                UUID.randomUUID(), "Bruno", Instant.now()
        );
    }

    @Test
    void descricaoDeCadaTipo() {
        assertThat(atividade(TipoAtividade.AVALIOU).descricao()).isEqualTo("Ana avaliou Hades com nota 5");
        assertThat(atividade(TipoAtividade.FAVORITOU).descricao()).isEqualTo("Ana favoritou Hades");
        assertThat(atividade(TipoAtividade.QUER_JOGAR).descricao()).isEqualTo("Ana quer jogar Hades");
        assertThat(atividade(TipoAtividade.COMECOU_A_SEGUIR).descricao()).isEqualTo("Ana começou a seguir Bruno");
    }
}
