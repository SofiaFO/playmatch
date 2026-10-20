package com.playmatch.social.domain;


import java.time.Instant;
import java.util.UUID;

/**
 * The Domain Record Atividade
 *
 * Um item do feed. Os campos de jogo ficam nulos em {@link TipoAtividade#COMECOU_A_SEGUIR},
 * e os de usuário seguido ficam nulos nos demais tipos.
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
public record Atividade(
        TipoAtividade tipo,
        UUID autorId,
        String autorNome,
        Long jogoId,
        String jogoNome,
        String jogoImagemUrl,
        Integer nota,
        String resenha,
        UUID usuarioSeguidoId,
        String usuarioSeguidoNome,
        Instant data
) {

    public String descricao() {
        return switch (tipo) {
            case AVALIOU -> "%s avaliou %s com nota %d".formatted(autorNome, jogoNome, nota);
            case FAVORITOU -> "%s favoritou %s".formatted(autorNome, jogoNome);
            case QUER_JOGAR -> "%s quer jogar %s".formatted(autorNome, jogoNome);
            case COMECOU_A_SEGUIR -> "%s começou a seguir %s".formatted(autorNome, usuarioSeguidoNome);
        };
    }
}
