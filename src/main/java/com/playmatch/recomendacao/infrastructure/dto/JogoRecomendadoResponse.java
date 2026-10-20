package com.playmatch.recomendacao.infrastructure.dto;


import com.playmatch.recomendacao.domain.JogoRecomendado;

/**
 * The Record JogoRecomendadoResponse
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
public record JogoRecomendadoResponse(
        Long jogoId,
        String nome,
        String imagemUrl,
        Double notaRawg,
        Double pontuacao,
        String motivo
) {

    public static JogoRecomendadoResponse fromDomain(JogoRecomendado jogo) {
        return new JogoRecomendadoResponse(
                jogo.jogoId(),
                jogo.nome(),
                jogo.imagemUrl(),
                jogo.notaRawg(),
                Math.round(jogo.pontuacao() * 100) / 100.0,
                jogo.motivo()
        );
    }
}
