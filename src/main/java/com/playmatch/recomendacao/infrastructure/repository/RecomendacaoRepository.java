package com.playmatch.recomendacao.infrastructure.repository;


import com.playmatch.recomendacao.domain.JogoRecomendado;
import com.playmatch.recomendacao.domain.UsuarioCompativel;

import java.util.List;
import java.util.UUID;

/**
 * The Repository Port RecomendacaoRepository
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
public interface RecomendacaoRepository {

    /** Jogos que compartilham gêneros e plataformas com os jogos que o usuário avaliou bem. */
    List<JogoRecomendado> recomendarPorPerfil(UUID usuarioId, int notaMinima, int limite);

    /** Jogos bem avaliados por usuários que deram notas parecidas às do usuário. */
    List<JogoRecomendado> recomendarPorJogadoresParecidos(UUID usuarioId, int notaMinima, int limite);

    /** Jogos mais bem avaliados no catálogo, excluindo os que o usuário já avaliou. */
    List<JogoRecomendado> buscarPopulares(UUID usuarioId, int limite);

    /** Jogos que compartilham gêneros e plataformas com um jogo de referência. */
    List<JogoRecomendado> buscarSimilares(Long jogoId, int limite);

    /** Usuários que avaliaram os mesmos jogos com notas parecidas. */
    List<UsuarioCompativel> buscarUsuariosCompativeis(UUID usuarioId, int limite);
}
