package com.playmatch.usuario.infrastructure.repository;


import com.playmatch.usuario.domain.Usuario;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * The Interface Port UsuarioRepository
 *
 * @author Sofia Ferreira de Oliveira
 * @since 01/09/2026
 */
public interface UsuarioRepository extends Neo4jRepository<Usuario, UUID> {

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, UUID id);
}
