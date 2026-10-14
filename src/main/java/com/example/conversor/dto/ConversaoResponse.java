package com.example.conversor.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Resposta que a nossa API devolve ao cliente.
 */
public record ConversaoResponse(
        String de,
        String para,
        BigDecimal valorOriginal,
        BigDecimal taxa,
        BigDecimal valorConvertido,
        LocalDate dataCotacao) {
}
