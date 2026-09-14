package com.playmatch.shared.application.security;


/**
 * The Interface SenhaEncoder
 *
 * @author Sofia Ferreira de Oliveira
 * @since 02/09/2026
 */

public interface SenhaEncoder {

    String codificar(String senha);

    boolean corresponde(String senha, String senhaHash);
}
