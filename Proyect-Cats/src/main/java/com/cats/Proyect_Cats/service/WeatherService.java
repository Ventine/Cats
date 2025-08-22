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
public class WeatherService {

    @Value("${openweathermap.api.key}")
    private String apiKey;

    private static final String BASE_URL =
            "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&units=metric&lang=es";

    public Map<String, Object> getWeather(String city) {
        RestTemplate restTemplate = new RestTemplate();

        String url = String.format(BASE_URL, city, apiKey);
        System.out.println("[DEBUG] URL construida: " + url);

        Map<String, Object> apiResponse = restTemplate.getForObject(url, Map.class);
        System.out.println("[DEBUG] Respuesta cruda del API: " + apiResponse);

        Map<String, Object> response = new HashMap<>();

        if (apiResponse != null) {
            Map<String, Object> main = (Map<String, Object>) apiResponse.get("main");
            Map<String, Object> wind = (Map<String, Object>) apiResponse.get("wind");
            Map<String, Object> sys = (Map<String, Object>) apiResponse.get("sys");

            response.put("city", apiResponse.get("name"));
            response.put("country", sys != null ? sys.get("country") : "N/A");
            response.put("temperature", main != null ? main.get("temp") : "N/A");
            response.put("feels_like", main != null ? main.get("feels_like") : "N/A");
            response.put("humidity", main != null ? main.get("humidity") : "N/A");
            response.put("pressure", main != null ? main.get("pressure") : "N/A");
            response.put("wind_speed", wind != null ? wind.get("speed") : "N/A");
            response.put("wind_deg", wind != null ? wind.get("deg") : "N/A");

            // 🔥 Refactor del timestamp
            if (apiResponse.get("dt") != null) {
                long epochSeconds = ((Number) apiResponse.get("dt")).longValue();
                LocalDateTime dateTime = LocalDateTime.ofInstant(
                        Instant.ofEpochSecond(epochSeconds),
                        ZoneId.systemDefault()
                );
                String formattedDate = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                response.put("timestamp", formattedDate);
            } else {
                response.put("timestamp", "N/A");
            }
        }

        System.out.println("[DEBUG] Datos procesados: " + response);
        return response;
    }
}
