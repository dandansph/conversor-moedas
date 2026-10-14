package com.example.conversor.service;

import com.example.conversor.client.FrankfurterClient;
import com.example.conversor.dto.ConversaoResponse;
import com.example.conversor.dto.FrankfurterResponse;
import com.example.conversor.exception.MoedaNaoSuportadaException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;

@Service
public class ConversorService {

    private final FrankfurterClient client;

    public ConversorService(FrankfurterClient client) {
        this.client = client;
    }

    public ConversaoResponse converter(String de, String para, BigDecimal valor) {
        String origem = de.toUpperCase();
        String destino = para.toUpperCase();

        BigDecimal taxa;
        LocalDate data;

        if (origem.equals(destino)) {
            // Mesma moeda: não precisa consultar a API
            taxa = BigDecimal.ONE;
            data = LocalDate.now();
        } else {
            FrankfurterResponse resposta = client.buscarTaxa(origem, destino);
            taxa = resposta.rates() == null ? null : resposta.rates().get(destino);
            if (taxa == null) {
                throw new MoedaNaoSuportadaException("Cotação indisponível para " + origem + " -> " + destino);
            }
            data = resposta.date();
        }

        BigDecimal convertido = valor.multiply(taxa).setScale(2, RoundingMode.HALF_UP);
        return new ConversaoResponse(origem, destino, valor, taxa, convertido, data);
    }

    public Map<String, String> listarMoedas() {
        return client.listarMoedas();
    }
}
