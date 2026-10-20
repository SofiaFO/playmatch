package com.playmatch.jogo.infrastructure.dto;


import com.playmatch.jogo.domain.EstatisticasAvaliacao;
import com.playmatch.jogo.domain.Genero;
import com.playmatch.jogo.domain.Jogo;
import com.playmatch.jogo.domain.Plataforma;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

/**
 * The Record JogoResponse
 *
 * @author Sofia Ferreira de Oliveira
 * @since 26/08/2026
 */
@Builder
public record JogoResponse(

        Long id,

        String nome,

        Integer ano,

        String descricao,

        LocalDate lancamento,

        String imagemUrl,

        Double notaRawg,

        Double mediaPlaymatch,

        Long totalAvaliacoes,

        List<String> generos,

        List<String> plataformas
) {

    public static JogoResponse fromDomain(Jogo jogo, EstatisticasAvaliacao estatisticas) {
        return JogoResponse.builder()
                .id(jogo.getId())
                .nome(jogo.getNome())
                .ano(jogo.getAno())
                .descricao(jogo.getDescricao())
                .lancamento(jogo.getLancamento())
                .imagemUrl(jogo.getImagemUrl())
                .notaRawg(jogo.getNotaRawg())
                .mediaPlaymatch(estatisticas.media())
                .totalAvaliacoes(estatisticas.total())
                .generos(
                        jogo.getGeneros().stream()
                                .map(Genero::getNome)
                                .sorted()
                                .toList()
                )
                .plataformas(
                        jogo.getPlataformas().stream()
                                .map(Plataforma::getNome)
                                .sorted()
                                .toList()
                )
                .build();
    }
}
