package com.example.conversor.exception;

public class MoedaNaoSuportadaException extends RuntimeException {

    public MoedaNaoSuportadaException(String mensagem) {
        super(mensagem);
    }
}
