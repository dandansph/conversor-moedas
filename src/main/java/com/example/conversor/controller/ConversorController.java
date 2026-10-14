package com.example.conversor.controller;

import com.example.conversor.dto.ConversaoResponse;
import com.example.conversor.service.ConversorService;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Validated
public class ConversorController {

    private final ConversorService service;

    public ConversorController(ConversorService service) {
        this.service = service;
    }

    /** Exemplo: GET /api/converter?de=USD&para=BRL&valor=100 */
    @GetMapping("/converter")
    public ConversaoResponse converter(
            @RequestParam
            @Pattern(regexp = "^[A-Za-z]{3}$", message = "'de' deve ter 3 letras (ex.: USD)")
            String de,

            @RequestParam
            @Pattern(regexp = "^[A-Za-z]{3}$", message = "'para' deve ter 3 letras (ex.: BRL)")
            String para,

            @RequestParam
            @Positive(message = "'valor' deve ser maior que zero")
            BigDecimal valor) {

        return service.converter(de, para, valor);
    }

    /** Exemplo: GET /api/moedas */
    @GetMapping("/moedas")
    public Map<String, String> moedas() {
        return service.listarMoedas();
    }
}
