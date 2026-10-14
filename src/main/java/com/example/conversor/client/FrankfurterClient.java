package com.example.conversor.client;

import com.example.conversor.dto.FrankfurterResponse;
import com.example.conversor.exception.ApiExternaException;
import com.example.conversor.exception.MoedaNaoSuportadaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

/**
 * Cliente HTTP da API pública Frankfurter (https://frankfurter.dev).
 */
@Component
public class FrankfurterClient {

    private final RestClient restClient;

    public FrankfurterClient(RestClient.Builder builder,
                             @Value("${conversor.api.base-url}") String baseUrl) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    /** Busca a cotação mais recente de {@code de} para {@code para}. */
    public FrankfurterResponse buscarTaxa(String de, String para) {
        try {
            return restClient.get()
                    .uri(uri -> uri.path("/latest")
                            .queryParam("base", de)
                            .queryParam("symbols", para)
                            .build())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                        throw new MoedaNaoSuportadaException(
                                "Não foi possível obter a cotação de " + de + " para " + para
                                        + ". Verifique se os códigos das moedas são válidos.");
                    })
                    .body(FrankfurterResponse.class);
        } catch (MoedaNaoSuportadaException ex) {
            throw ex;
        } catch (RestClientException ex) {
            throw new ApiExternaException("Serviço de câmbio indisponível no momento.", ex);
        }
    }

    /** Lista as moedas suportadas (código -> nome). */
    public Map<String, String> listarMoedas() {
        try {
            return restClient.get()
                    .uri("/currencies")
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, String>>() {});
        } catch (RestClientException ex) {
            throw new ApiExternaException("Serviço de câmbio indisponível no momento.", ex);
        }
    }
}
