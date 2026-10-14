package com.example.conversor.controller;

import com.example.conversor.client.FrankfurterClient;
import com.example.conversor.dto.FrankfurterResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ConversorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FrankfurterClient client;

    @Test
    void converteComSucesso() throws Exception {
        when(client.buscarTaxa("USD", "BRL")).thenReturn(new FrankfurterResponse(
                BigDecimal.ONE, "USD", LocalDate.of(2026, 10, 13), Map.of("BRL", new BigDecimal("5.40"))));

        mockMvc.perform(get("/api/converter").param("de", "USD").param("para", "BRL").param("valor", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valorConvertido").value(540.00))
                .andExpect(jsonPath("$.de").value("USD"))
                .andExpect(jsonPath("$.para").value("BRL"));
    }

    @Test
    void rejeitaCodigoDeMoedaInvalido() throws Exception {
        mockMvc.perform(get("/api/converter").param("de", "DOLAR").param("para", "BRL").param("valor", "100"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void rejeitaValorNegativo() throws Exception {
        mockMvc.perform(get("/api/converter").param("de", "USD").param("para", "BRL").param("valor", "-5"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejeitaQuandoFaltaParametro() throws Exception {
        mockMvc.perform(get("/api/converter").param("de", "USD").param("para", "BRL"))
                .andExpect(status().isBadRequest());
    }
}
