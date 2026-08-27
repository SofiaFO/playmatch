package com.playmatch.jogo.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

/**
 * The Domain Class Genero
 *
 * @author Sofia Ferreira de Oliveira
 * @since 26/08/2026
 */

@Getter
@NoArgsConstructor
@Node("Genero")
public class Genero {

    @Id
    private String nome;
}
