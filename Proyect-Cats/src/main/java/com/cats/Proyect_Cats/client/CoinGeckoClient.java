// client/CoinGeckoClient.java
package com.cats.Proyect_Cats.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.cats.Proyect_Cats.DTO.CryptoResponseDto;

import java.util.List;
import java.util.Map;

@Component
public class CoinGeckoClient {

    private final RestClient restClient;

    public CoinGeckoClient(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("https://api.coingecko.com/api/v3").build();
    }

    public List<CryptoResponseDto> getCryptos() {
        try {
            List<Map<String, Object>> response = restClient.get()
                    .uri("/coins/markets?vs_currency=usd&order=market_cap_desc&per_page=5&page=1&sparkline=false")
                    .retrieve()
                    .body(List.class);

            // mapear a DTO manualmente
            return response.stream().map(item -> {
                CryptoResponseDto dto = new CryptoResponseDto();
                dto.setId((String) item.get("id"));
                dto.setSymbol((String) item.get("symbol"));
                dto.setName((String) item.get("name"));
                dto.setCurrentPrice(
                        item.get("current_price") != null ? ((Number) item.get("current_price")).doubleValue() : 0
                );
                return dto;
            }).toList();

        } catch (RestClientException e) {
            throw new RuntimeException("Error al consumir CoinGecko API", e);
        }
    }
}
