package com.playmatch.usuario.infrastructure.dto.lista;


import com.playmatch.jogo.domain.Genero;
import com.playmatch.usuario.domain.JogoSalvo;

import java.time.Instant;
import java.util.List;

/**
 * The Record JogoSalvoResponse
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
public record JogoSalvoResponse(
        Long jogoId,
        String nome,
        String imagemUrl,
        List<String> generos,
        Instant adicionadoEm
) {

    public static JogoSalvoResponse fromDomain(JogoSalvo salvo) {
        return new JogoSalvoResponse(
                salvo.getJogo().getId(),
                salvo.getJogo().getNome(),
                salvo.getJogo().getImagemUrl(),
                salvo.getJogo().getGeneros().stream()
                        .map(Genero::getNome)
                        .sorted()
                        .toList(),
                salvo.getAdicionadoEm()
        );
    }
}
