package com.playmatch.usuario.infrastructure.repository;


import com.playmatch.usuario.domain.EstatisticasPerfil;

import java.util.UUID;

/**
 * The Repository Port PerfilRepository
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
public interface PerfilRepository {

    EstatisticasPerfil buscarEstatisticas(UUID usuarioId, UUID visitanteId);
}
