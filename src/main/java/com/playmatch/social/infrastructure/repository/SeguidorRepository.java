package com.playmatch.social.infrastructure.repository;


import com.playmatch.social.domain.UsuarioConectado;

import java.util.List;
import java.util.UUID;

/**
 * The Repository Port SeguidorRepository
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
public interface SeguidorRepository {

    /** Cria a relação SEGUE; se ela já existe, nada muda (inclusive a data). */
    void seguir(UUID seguidorId, UUID seguidoId);

    /** @return {@code true} se a relação existia e foi removida */
    boolean deixarDeSeguir(UUID seguidorId, UUID seguidoId);

    List<UsuarioConectado> listarSeguidores(UUID usuarioId, int pagina, int tamanho);

    List<UsuarioConectado> listarSeguindo(UUID usuarioId, int pagina, int tamanho);

    long contarSeguidores(UUID usuarioId);

    long contarSeguindo(UUID usuarioId);
}
