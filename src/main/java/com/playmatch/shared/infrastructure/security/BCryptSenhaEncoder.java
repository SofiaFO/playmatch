package com.playmatch.shared.infrastructure.security;


import com.playmatch.shared.application.security.SenhaEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * The Adapter Class BCryptSenhaEncoder
 *
 * @author Sofia Ferreira de Oliveira
 * @since 02/09/2026
 */

@Component
@RequiredArgsConstructor
public class BCryptSenhaEncoder implements SenhaEncoder {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String codificar(String senha) {
        return passwordEncoder.encode(senha);
    }

    @Override
    public boolean corresponde(String senha, String senhaHash) {
        return passwordEncoder.matches(senha, senhaHash);
    }
}
