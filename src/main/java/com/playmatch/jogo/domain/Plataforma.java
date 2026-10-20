package com.playmatch.jogo.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

/**
 * The Domain Class Plataforma
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Node("Plataforma")
public class Plataforma {

    @Id
    private String nome;
}
