package com.playmatch.jogo.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

/**
 * The Domain Class Jogo
 *
 * @author Sofia Ferreira de Oliveira
 * @since 26/08/2026
 */

@Getter
@NoArgsConstructor
@Node("Jogo")
public class Jogo {

    @Id
    private Long id;
    private String nome;
    private Integer ano;

    @Relationship(
            type = "TEM_GENERO",
            direction = Relationship.Direction.OUTGOING
    )
    private Set<Genero> generos = new HashSet<>();
}
