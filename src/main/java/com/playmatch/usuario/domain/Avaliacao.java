package com.playmatch.usuario.domain;

import com.playmatch.jogo.domain.Jogo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.RelationshipId;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

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

    @RelationshipId
    private Long id;

    private Integer nota;

    @TargetNode
    private Jogo jogo;

    public Avaliacao(Integer nota, Jogo jogo) {
        this.nota = nota;
        this.jogo = jogo;
    }

    public void atualizarNota(Integer nota){
        this.nota = nota;
    }
}
