package com.playmatch.social.infrastructure.dto;


import com.playmatch.social.domain.Atividade;
import com.playmatch.social.domain.TipoAtividade;

import java.time.Instant;
import java.util.UUID;

/**
 * The Record AtividadeResponse
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
public record AtividadeResponse(
        TipoAtividade tipo,
        String descricao,
        UsuarioResumo autor,
        JogoResumo jogo,
        Integer nota,
        String resenha,
        UsuarioResumo usuarioSeguido,
        Instant data
) {

    public record UsuarioResumo(UUID id, String nome) {
    }

    public record JogoResumo(Long id, String nome, String imagemUrl) {
    }

    public static AtividadeResponse fromDomain(Atividade atividade) {
        return new AtividadeResponse(
                atividade.tipo(),
                atividade.descricao(),
                new UsuarioResumo(atividade.autorId(), atividade.autorNome()),
                atividade.jogoId() == null
                        ? null
                        : new JogoResumo(atividade.jogoId(), atividade.jogoNome(), atividade.jogoImagemUrl()),
                atividade.nota(),
                atividade.resenha(),
                atividade.usuarioSeguidoId() == null
                        ? null
                        : new UsuarioResumo(atividade.usuarioSeguidoId(), atividade.usuarioSeguidoNome()),
                atividade.data()
        );
    }
}
