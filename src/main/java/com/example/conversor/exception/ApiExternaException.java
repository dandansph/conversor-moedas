package com.example.conversor.exception;

public class ApiExternaException extends RuntimeException {

    public ApiExternaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
