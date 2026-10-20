package com.playmatch.usuario.domain;

import com.playmatch.jogo.domain.Jogo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.Instant;

/**
 * The Domain Class JogoSalvo
 *
 * Um jogo que o usuário guardou em uma de suas listas (favoritos ou lista de desejos).
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */

@Getter
@NoArgsConstructor
@RelationshipProperties
public class JogoSalvo {

    @RelationshipId
    private Long id;

    private Instant adicionadoEm;

    @TargetNode
    private Jogo jogo;

    public JogoSalvo(Jogo jogo) {
        this.jogo = jogo;
        this.adicionadoEm = Instant.now();
    }
}
