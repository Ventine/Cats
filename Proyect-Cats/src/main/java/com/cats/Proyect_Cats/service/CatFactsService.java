package com.cats.Proyect_Cats.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class CatFactsService {

    private static final String CAT_FACTS_URL = "https://catfact.ninja/fact";

    public String getRandomFact() {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(CAT_FACTS_URL, Map.class);
        return response != null ? (String) response.get("fact") : "No se pudo obtener un dato curioso.";
    }
}
