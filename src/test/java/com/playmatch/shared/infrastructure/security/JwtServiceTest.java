package com.playmatch.shared.infrastructure.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String SEGREDO = "segredo-de-teste-com-pelo-menos-32-bytes!!";

    private final JwtService jwtService = new JwtService(SEGREDO, 60_000);

    @Test
    void tokenGeradoDevolveOIdDoUsuario() {
        UUID usuarioId = UUID.randomUUID();

        String token = jwtService.gerarToken(usuarioId);

        assertThat(jwtService.extrairUsuarioId(token)).contains(usuarioId);
    }

    @Test
    void tokenAdulteradoEhRecusado() {
        String token = jwtService.gerarToken(UUID.randomUUID()) + "x";

        assertThat(jwtService.extrairUsuarioId(token)).isEmpty();
    }

    @Test
    void tokenAntigoComEmailNoSubjectEhRecusado() {
        String tokenAntigo = Jwts.builder()
                .subject("sofia@email.com")
                .signWith(Keys.hmacShaKeyFor(SEGREDO.getBytes(StandardCharsets.UTF_8)))
                .compact();

        assertThat(jwtService.extrairUsuarioId(tokenAntigo)).isEmpty();
    }
}
