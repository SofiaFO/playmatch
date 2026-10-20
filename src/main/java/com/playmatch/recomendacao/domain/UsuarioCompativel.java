package com.playmatch.recomendacao.domain;


import java.util.List;
import java.util.UUID;

/**
 * The Domain Record UsuarioCompativel
 *
 * @param jogosEmComum   quantidade de jogos que ambos avaliaram
 * @param compatibilidade percentual (0 a 100) de concordância entre as notas dadas
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
public record UsuarioCompativel(
        UUID usuarioId,
        String nome,
        long jogosEmComum,
        int compatibilidade,
        List<String> exemplos
) {

    private static final double MAIOR_DIFERENCA_POSSIVEL = 4.0;

    /**
     * Converte a diferença média entre as notas (0 a 4, numa escala de 1 a 5)
     * em um percentual de compatibilidade.
     */
    public static int calcularCompatibilidade(double diferencaMedia) {
        double diferenca = Math.clamp(diferencaMedia, 0.0, MAIOR_DIFERENCA_POSSIVEL);

        return (int) Math.round((1 - diferenca / MAIOR_DIFERENCA_POSSIVEL) * 100);
    }
}
