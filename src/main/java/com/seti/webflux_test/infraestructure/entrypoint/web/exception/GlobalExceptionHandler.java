package com.seti.webflux_test.infraestructure.entrypoint.web.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import lombok.Getter;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @Getter
    private static class ErrorResponse {

        private final int status;
        private final String message;
        private final LocalDateTime timestamp;

        public ErrorResponse(HttpStatus status, String message) {
            this.status = status.value();
            this.message = message;
            this.timestamp = LocalDateTime.now();
        }
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidationExceptions(WebExchangeBindException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce("", (acc, msg) -> acc.isEmpty() ? msg : acc + "; " + msg);
        return Mono.just(ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST, errorMessage)));
    }

    @ExceptionHandler(CustomException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleCustomException(CustomException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(
                HttpStatus.BAD_REQUEST, ex.getMessage())));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR, "Error en el formulario, una de las relaciones que intenta asociar no existe.")));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGenericException(Exception ex) {
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR, "Error inesperado, intentelo más tarde.")));
    }
}