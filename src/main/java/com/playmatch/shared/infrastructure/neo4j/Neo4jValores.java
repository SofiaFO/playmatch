package com.playmatch.shared.infrastructure.neo4j;


import org.neo4j.driver.Value;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * The Utility Class Neo4jValores
 *
 * Conversões null-safe dos valores devolvidos pelo driver do Neo4j.
 *
 * @author Sofia Ferreira de Oliveira
 * @since 19/10/2026
 */
public final class Neo4jValores {

    private Neo4jValores() {
    }

    public static String texto(Value valor) {
        return valor.isNull() ? null : valor.asString();
    }

    public static Double decimal(Value valor) {
        return valor.isNull() ? null : valor.asDouble();
    }

    public static Integer inteiro(Value valor) {
        return valor.isNull() ? null : valor.asInt();
    }

    public static Long longo(Value valor) {
        return valor.isNull() ? null : valor.asLong();
    }

    public static UUID uuid(Value valor) {
        return valor.isNull() ? null : UUID.fromString(valor.asString());
    }

    public static Instant instante(Value valor) {
        if (valor.isNull()) {
            return null;
        }

        return switch (valor.asObject()) {
            case ZonedDateTime data -> data.toInstant();
            case OffsetDateTime data -> data.toInstant();
            case LocalDateTime data -> data.toInstant(ZoneOffset.UTC);
            case Instant data -> data;
            default -> throw new IllegalStateException("Valor temporal inesperado: " + valor.type().name());
        };
    }

    /** Converte página/tamanho no SKIP do Cypher. */
    public static long deslocamento(int pagina, int tamanho) {
        return (long) pagina * tamanho;
    }
}
