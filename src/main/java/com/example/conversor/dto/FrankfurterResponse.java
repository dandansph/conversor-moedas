package com.example.conversor.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * Resposta da API Frankfurter, por exemplo:
 * {"amount":1.0,"base":"USD","date":"2026-10-13","rates":{"BRL":5.43}}
 */
public record FrankfurterResponse(
        BigDecimal amount,
        String base,
        LocalDate date,
        Map<String, BigDecimal> rates) {
}
