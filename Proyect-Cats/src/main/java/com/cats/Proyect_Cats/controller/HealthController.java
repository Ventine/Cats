package com.cats.Proyect_Cats.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.cats.Proyect_Cats.service.WeatherService;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    private final WeatherService weatherService;

    public HealthController(WeatherService weatherService) {
        this.weatherService = weatherService;
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

}
