package com.example.conversor.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Tratamento global de erros: transforma exceções em respostas JSON padronizadas.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail tratarValidacao(ConstraintViolationException ex) {
        String detalhes = ex.getConstraintViolations().stream()
                .map(v -> v.getMessage())
                .collect(Collectors.joining("; "));
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detalhes);
        problema.setTitle("Parâmetros inválidos");
        return problema;
    }

    @ExceptionHandler(MoedaNaoSuportadaException.class)
    public ProblemDetail tratarMoedaNaoSuportada(MoedaNaoSuportadaException ex) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problema.setTitle("Moeda não suportada");
        return problema;
    }

    @ExceptionHandler(ApiExternaException.class)
    public ProblemDetail tratarApiExterna(ApiExternaException ex) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_GATEWAY, ex.getMessage());
        problema.setTitle("Falha ao consultar o serviço de câmbio");
        return problema;
    }
}
