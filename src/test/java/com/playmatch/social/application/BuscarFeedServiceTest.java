package com.playmatch.social.application;

import com.playmatch.shared.application.security.UsuarioAutenticado;
import com.playmatch.social.domain.Atividade;
import com.playmatch.social.domain.TipoAtividade;
import com.playmatch.social.infrastructure.dto.FeedResponse;
import com.playmatch.social.infrastructure.repository.FeedRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarFeedServiceTest {

    @Mock
    private FeedRepository feedRepository;

    @Mock
    private UsuarioAutenticado usuarioAutenticado;

    @InjectMocks
    private BuscarFeedService service;

    private final UUID eu = UUID.randomUUID();

    private static final Atividade ATIVIDADE = new Atividade(
            TipoAtividade.FAVORITOU, UUID.randomUUID(), "Ana", 1L, "Hades", null, null, null, null, null, Instant.now()
    );

    @BeforeEach
    void setUp() {
        when(usuarioAutenticado.id()).thenReturn(eu);
    }

    @Test
    void buscaUmItemAMaisParaSaberSeHaProximaPagina() {
        when(feedRepository.buscarFeed(eu, 4, 3)).thenReturn(Collections.nCopies(3, ATIVIDADE));

        FeedResponse feed = service.executar(2, 2);

        assertThat(feed.itens()).hasSize(2);
        assertThat(feed.temMais()).isTrue();
    }

    @Test
    void ultimaPaginaNaoTemMais() {
        when(feedRepository.buscarFeed(eu, 0, 21)).thenReturn(List.of(ATIVIDADE));

        FeedResponse feed = service.executar(0, 20);

        assertThat(feed.itens()).hasSize(1);
        assertThat(feed.itens().getFirst().descricao()).isEqualTo("Ana favoritou Hades");
        assertThat(feed.itens().getFirst().usuarioSeguido()).isNull();
        assertThat(feed.temMais()).isFalse();
    }
}
