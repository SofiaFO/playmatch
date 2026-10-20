package com.playmatch.jogo.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The Domain Class Jogo
 *
 * O id é o mesmo identificador do jogo na RAWG, o que torna a importação idempotente.
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
    private String descricao;
    private LocalDate lancamento;
    private String imagemUrl;
    private Double notaRawg;

    @Relationship(
            type = "TEM_GENERO",
            direction = Relationship.Direction.OUTGOING
    )
    private Set<Genero> generos = new HashSet<>();

    @Relationship(
            type = "DISPONIVEL_EM",
            direction = Relationship.Direction.OUTGOING
    )
    private Set<Plataforma> plataformas = new HashSet<>();

    public static Jogo importar(
            Long id,
            String nome,
            String descricao,
            LocalDate lancamento,
            String imagemUrl,
            Double notaRawg,
            Collection<String> generos,
            Collection<String> plataformas
    ) {
        if (id == null) {
            throw new IllegalArgumentException("O ID do jogo é obrigatório.");
        }
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do jogo é obrigatório.");
        }

        Jogo jogo = new Jogo();
        jogo.id = id;
        jogo.nome = nome.trim();
        jogo.descricao = descricao;
        jogo.lancamento = lancamento;
        jogo.ano = lancamento != null ? lancamento.getYear() : null;
        jogo.imagemUrl = imagemUrl;
        jogo.notaRawg = notaRawg;
        jogo.generos = nomesValidos(generos).stream()
                .map(Genero::new)
                .collect(Collectors.toCollection(HashSet::new));
        jogo.plataformas = nomesValidos(plataformas).stream()
                .map(Plataforma::new)
                .collect(Collectors.toCollection(HashSet::new));

        return jogo;
    }

    private static Set<String> nomesValidos(Collection<String> nomes) {
        if (nomes == null) {
            return Set.of();
        }

        return nomes.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(nome -> !nome.isEmpty())
                .collect(Collectors.toSet());
    }
}
