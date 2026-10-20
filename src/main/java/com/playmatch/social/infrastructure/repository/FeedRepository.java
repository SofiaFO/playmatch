package com.playmatch.social.infrastructure.repository;


import com.playmatch.social.domain.Atividade;

import java.util.List;
import java.util.UUID;

/**
 * The Repository Port FeedRepository
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
public interface FeedRepository {

    /** Atividades de quem o usuário segue, das mais recentes para as mais antigas. */
    List<Atividade> buscarFeed(UUID usuarioId, long deslocamento, int limite);
}
