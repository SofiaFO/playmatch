package com.playmatch.usuario.domain;

import com.playmatch.jogo.domain.Jogo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.Instant;

/**
* The Domain Class Avaliacao
*
* @author Sofia Ferreira de Oliveira
* @since 01/09/2026
*/

@Getter
@NoArgsConstructor
@RelationshipProperties
public class Avaliacao {

    public static final int TAMANHO_MAXIMO_RESENHA = 2000;

    @RelationshipId
    private Long id;

    private Integer nota;

    private String resenha;

    private Instant criadaEm;

    private Instant atualizadaEm;

    @TargetNode
    private Jogo jogo;

    public Avaliacao(Integer nota, String resenha, Jogo jogo) {
        this.nota = nota;
        this.resenha = normalizarResenha(resenha);
        this.jogo = jogo;
        this.criadaEm = Instant.now();
        this.atualizadaEm = this.criadaEm;
    }

    public void atualizar(Integer nota, String resenha) {
        this.nota = nota;
        this.resenha = normalizarResenha(resenha);
        this.atualizadaEm = Instant.now();
    }

    static String normalizarResenha(String resenha) {
        if (resenha == null || resenha.isBlank()) {
            return null;
        }

        String texto = resenha.trim();

        if (texto.length() > TAMANHO_MAXIMO_RESENHA) {
            throw new IllegalArgumentException(
                    "A resenha deve conter no máximo %d caracteres.".formatted(TAMANHO_MAXIMO_RESENHA)
            );
        }

        return texto;
    }
}
