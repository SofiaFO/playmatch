package com.playmatch.jogo.infrastructure.repository;


import com.playmatch.jogo.domain.Jogo;
import org.springframework.data.neo4j.repository.Neo4jRepository;

/**
 * The Repository Port JogoRepository
 *
 * @author Sofia Ferreira de Oliveira
 * @since 26/08/2026
 */
public interface JogoRepository extends Neo4jRepository<Jogo, Long> {
}
