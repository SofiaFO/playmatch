package com.playmatch.shared.infrastructure.exception;


/**
 * The Class UsuarioNaoEncontradoException
 *
 * @author Sofia Ferreira de Oliveira
 * @since 01/09/2026
 */
public class UsuarioNaoEncontradoException extends RuntimeException {
    public UsuarioNaoEncontradoException(Long id ) {
        super("Usuário não encontrado: " + id);
    }
}
