package com.playmatch.shared.infrastructure.exception;

/**
 * The Exception JogoNaoEncontradoException
 *
 * @author Sofia Ferreira de Oliveira
 * @since 27/08/2026
 */
public class JogoNaoEncontradoException extends RuntimeException {

    public JogoNaoEncontradoException(Long id) {
        super("Jogo não encontrado com o id: " + id);
    }
}