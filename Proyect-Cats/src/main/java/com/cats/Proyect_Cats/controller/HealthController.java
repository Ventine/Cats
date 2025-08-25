package com.cats.Proyect_Cats.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cats.Proyect_Cats.service.WeatherService;
import com.cats.Proyect_Cats.service.JokesService;
import com.cats.Proyect_Cats.service.CatFactsService;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    private final WeatherService weatherService;
    private final CatFactsService catFactsService;
    private final JokesService jokesService;

    public HealthController(WeatherService weatherService, CatFactsService catFactsService, JokesService jokesService) {
        this.weatherService = weatherService;
        this.catFactsService = catFactsService;
        this.jokesService = jokesService;
    }

    @GetMapping("/health")
    public Map<String, Object> getHealth() throws UnknownHostException {
        Map<String, Object> response = new HashMap<>();

        response.put("status", "ONLINE");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("service", "health-service");
        response.put("version", "1.0.0");
        response.put("host", InetAddress.getLocalHost().getHostName());
        response.put("ip", InetAddress.getLocalHost().getHostAddress());
        response.put("javaVersion", System.getProperty("java.version"));
        response.put("os", System.getProperty("os.name") + " " + System.getProperty("os.version"));

        return response;
    }

    @GetMapping("/weather/{city}")
    public Map<String, Object> getWeather(@PathVariable String city) {
        System.out.println("[DEBUG] Llamada recibida para ciudad: " + city);
        Map<String, Object> weatherData = weatherService.getWeather(city);
        System.out.println("[DEBUG] Datos finales construidos: " + weatherData);
        return weatherData;
    }

    @GetMapping("/catfact")
    public Map<String, Object> getCatFact() {
        return Map.of("fact", catFactsService.getRandomFact());
    }

    @GetMapping("/joke")
    public Map<String, Object> getJoke() {
        return Map.of("joke", jokesService.getRandomJoke());
    }

}
