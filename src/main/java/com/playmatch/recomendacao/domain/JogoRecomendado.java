package com.playmatch.recomendacao.domain;


import java.util.List;
import java.util.Objects;

/**
 * The Domain Record JogoRecomendado
 *
 * @param pontuacao relevância relativa dentro de uma mesma lista de recomendações
 * @param motivo    explicação legível de por que o jogo foi recomendado
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
public record JogoRecomendado(
        Long jogoId,
        String nome,
        String imagemUrl,
        Double notaRawg,
        double pontuacao,
        String motivo
) {

    public static JogoRecomendado porGeneros(
            Long jogoId,
            String nome,
            String imagemUrl,
            Double notaRawg,
            double pontuacao,
            List<String> generosEmComum
    ) {
        List<String> generos = generosEmComum == null ? List.of() : generosEmComum.stream()
                .filter(Objects::nonNull)
                .sorted()
                .toList();

        String motivo = generos.isEmpty()
                ? "Disponível nas plataformas dos jogos que você curte"
                : "Porque você curte " + juntar(generos);

        return new JogoRecomendado(jogoId, nome, imagemUrl, notaRawg, pontuacao, motivo);
    }

    public static JogoRecomendado porSimilaridade(
            Long jogoId,
            String nome,
            String imagemUrl,
            Double notaRawg,
            double pontuacao,
            List<String> generosEmComum
    ) {
        List<String> generos = generosEmComum == null ? List.of() : generosEmComum.stream()
                .filter(Objects::nonNull)
                .sorted()
                .toList();

        String motivo = generos.isEmpty()
                ? "Disponível nas mesmas plataformas"
                : "Também é " + juntar(generos);

        return new JogoRecomendado(jogoId, nome, imagemUrl, notaRawg, pontuacao, motivo);
    }

    public static JogoRecomendado porJogadoresParecidos(
            Long jogoId,
            String nome,
            String imagemUrl,
            Double notaRawg,
            double pontuacao,
            long jogadores
    ) {
        String motivo = jogadores == 1
                ? "Bem avaliado por 1 jogador com gosto parecido com o seu"
                : "Bem avaliado por %d jogadores com gosto parecido com o seu".formatted(jogadores);

        return new JogoRecomendado(jogoId, nome, imagemUrl, notaRawg, pontuacao, motivo);
    }

    public static JogoRecomendado popular(
            Long jogoId,
            String nome,
            String imagemUrl,
            Double notaRawg,
            double pontuacao
    ) {
        return new JogoRecomendado(
                jogoId, nome, imagemUrl, notaRawg, pontuacao,
                "Popular entre os jogadores do PlayMatch"
        );
    }

    private static String juntar(List<String> itens) {
        if (itens.size() == 1) {
            return itens.getFirst();
        }

        return String.join(", ", itens.subList(0, itens.size() - 1)) + " e " + itens.getLast();
    }
}
