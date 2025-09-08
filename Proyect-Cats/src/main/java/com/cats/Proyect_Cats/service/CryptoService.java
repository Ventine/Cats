// service/CryptoService.java
package com.cats.Proyect_Cats.service;

import org.springframework.stereotype.Service;

import com.cats.Proyect_Cats.DTO.CryptoResponseDto;
import com.cats.Proyect_Cats.client.CoinGeckoClient;

import java.util.List;

@Service
public class CryptoService {

    private final CoinGeckoClient client;

    public CryptoService(CoinGeckoClient client) {
        this.client = client;
    }

    public List<CryptoResponseDto> getTopCryptos() {
        return client.getCryptos();
    }
}
