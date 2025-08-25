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
public class JokesService {

    private static final String JOKE_URL = "https://official-joke-api.appspot.com/random_joke";

    public String getRandomJoke() {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(JOKE_URL, Map.class);

        if (response != null) {
            return response.get("setup") + " " + response.get("punchline");
        }
        return "No se pudo obtener un chiste.";
    }
}
