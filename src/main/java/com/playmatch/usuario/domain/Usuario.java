package com.playmatch.usuario.domain;


import com.playmatch.jogo.domain.Jogo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * The Domain Class Usuario
 *
 * @author Sofia Ferreira de Oliveira
 * @since 01/09/2026
 */

@Getter
@NoArgsConstructor
@Node("Usuario")
public class Usuario {

    @Id
    private Long id;

    private String nome;

    @Relationship(
            type = "AVALIOU",
            direction = Relationship.Direction.OUTGOING
    )
    private Set<Avaliacao> avaliacoes = new HashSet<>();

    public void avaliar(Jogo jogo, Integer nota) {
        avaliacoes.stream()
                .filter(avaliacao ->
                        Objects.equals(
                                avaliacao.getJogo().getId(),
                                jogo.getId()
                        )
                )
                .findFirst()
                .ifPresentOrElse(
                        avaliacao -> avaliacao.atualizarNota(nota),
                        () -> avaliacoes.add(new Avaliacao(nota, jogo))
                );
    }
}
