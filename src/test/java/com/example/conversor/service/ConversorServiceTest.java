package com.example.conversor.service;

import com.example.conversor.client.FrankfurterClient;
import com.example.conversor.dto.ConversaoResponse;
import com.example.conversor.dto.FrankfurterResponse;
import com.example.conversor.exception.MoedaNaoSuportadaException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ConversorServiceTest {

    private final FrankfurterClient client = mock(FrankfurterClient.class);
    private final ConversorService service = new ConversorService(client);

    @Test
    void converteUsandoATaxaDaApi() {
        when(client.buscarTaxa("USD", "BRL")).thenReturn(new FrankfurterResponse(
                BigDecimal.ONE, "USD", LocalDate.of(2026, 10, 13), Map.of("BRL", new BigDecimal("5.40"))));

        ConversaoResponse resposta = service.converter("usd", "brl", new BigDecimal("100"));

        assertThat(resposta.de()).isEqualTo("USD");
        assertThat(resposta.para()).isEqualTo("BRL");
        assertThat(resposta.taxa()).isEqualByComparingTo("5.40");
        assertThat(resposta.valorConvertido()).isEqualByComparingTo("540.00");
        assertThat(resposta.dataCotacao()).isEqualTo(LocalDate.of(2026, 10, 13));
    }

    @Test
    void mesmaMoedaNaoConsultaAApi() {
        ConversaoResponse resposta = service.converter("EUR", "eur", new BigDecimal("50"));

        assertThat(resposta.valorConvertido()).isEqualByComparingTo("50.00");
        verifyNoInteractions(client);
    }

    @Test
    void lancaErroQuandoATaxaNaoVem() {
        when(client.buscarTaxa("USD", "XXX")).thenReturn(new FrankfurterResponse(
                BigDecimal.ONE, "USD", LocalDate.now(), Map.of()));

        assertThatThrownBy(() -> service.converter("USD", "XXX", BigDecimal.TEN))
                .isInstanceOf(MoedaNaoSuportadaException.class);
    }
}
