package com.playmatch.usuario.infrastructure;


import com.playmatch.usuario.domain.Usuario;
import org.springframework.data.neo4j.repository.Neo4jRepository;

/**
 * The Interface Port UsuarioRepository
 *
 * @author Sofia Ferreira de Oliveira
 * @since 01/09/2026
 */
public interface UsuarioRepository extends Neo4jRepository<Usuario, Long> {
}
