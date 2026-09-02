package com.playmatch.shared.exception;


import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * The Class BusinessException
 *
 * @author Sofia Ferreira de Oliveira
 * @since 02/09/2026
 */
@Getter
public class BusinessException extends RuntimeException {

    private final HttpStatus httpStatus;

    public BusinessException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public BusinessException(String message) {
        this(message, HttpStatus.UNPROCESSABLE_CONTENT);
    }
}
