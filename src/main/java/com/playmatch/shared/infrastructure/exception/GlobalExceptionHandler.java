package com.playmatch.shared.infrastructure.exception;

import com.playmatch.shared.exception.BusinessException;
import com.playmatch.shared.infrastructure.dto.ErroResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * The Exception Handler GlobalExceptionHandler
 *
 * @author Sofia Ferreira de Oliveira
 * @since 31/08/2026
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErroResponse> tratarBusinessException(
            BusinessException exception,
            HttpServletRequest request
    ) {
        HttpStatus status = exception.getHttpStatus();

        ErroResponse response = criarErro(
                status,
                status.getReasonPhrase(),
                exception.getMessage(),
                request
        );

        return ResponseEntity
                .status(status)
                .body(response);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErroResponse> tratarValidacao(
            HandlerMethodValidationException exception,
            HttpServletRequest request
    ) {
        String mensagem = exception.getAllErrors()
                .stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("Dados inválidos");

        ErroResponse response = criarErro(
                HttpStatus.BAD_REQUEST,
                "Requisição inválida",
                mensagem,
                request
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarValidacaoDeCorpo(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        String mensagem = exception.getBindingResult()
                .getAllErrors()
                .stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("Dados inválidos");

        ErroResponse response = criarErro(
                HttpStatus.BAD_REQUEST,
                "Requisição inválida",
                mensagem,
                request
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErroResponse> tratarParametroAusente(
            MissingServletRequestParameterException exception,
            HttpServletRequest request
    ) {
        String mensagem = "O parâmetro '%s' é obrigatório"
                .formatted(exception.getParameterName());

        ErroResponse response = criarErro(
                HttpStatus.BAD_REQUEST,
                "Requisição inválida",
                mensagem,
                request
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    private ErroResponse criarErro(
            HttpStatus status,
            String erro,
            String mensagem,
            HttpServletRequest request
    ) {
        return new ErroResponse(
                LocalDateTime.now(),
                status.value(),
                erro,
                mensagem,
                request.getRequestURI()
        );
    }
}
