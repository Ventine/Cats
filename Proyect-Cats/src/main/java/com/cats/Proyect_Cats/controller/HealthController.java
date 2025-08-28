package com.cats.Proyect_Cats.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.cats.Proyect_Cats.service.CatFactsService;
import com.cats.Proyect_Cats.service.JokesService;
import com.cats.Proyect_Cats.service.SystemInfoService;
import com.cats.Proyect_Cats.service.SystemInfoServiceMax;
import com.cats.Proyect_Cats.service.WeatherService;
import com.cats.Proyect_Cats.service.TranslationService;

import com.cats.Proyect_Cats.DTO.WordResponse;
import com.cats.Proyect_Cats.exception.DictionaryException;
import com.cats.Proyect_Cats.exception.TranslationException;

@RestController
public class HealthController {

    private final WeatherService weatherService;
    private final CatFactsService catFactsService;
    private final JokesService jokesService;
    private final SystemInfoService systemInfoService;
    private final SystemInfoServiceMax systemInfoServiceMax;
    private final TranslationService translationService;

    public HealthController(WeatherService weatherService, CatFactsService catFactsService, 
    JokesService jokesService, SystemInfoService systemInfoService, SystemInfoServiceMax systemInfoServiceMax,
    TranslationService translationService) {
        this.weatherService = weatherService;
        this.catFactsService = catFactsService;
        this.jokesService = jokesService;
        this.systemInfoService = systemInfoService;
        this.systemInfoServiceMax = systemInfoServiceMax;
        this.translationService = translationService;

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

    @GetMapping("/systemInfo")
    public Map<String, Object> getSystemInfo() {
        return systemInfoService.getSystemInfo();
    }

    @GetMapping("/systemInfoMax")
    public Map<String, Object> getSystemInfoMax() {
        return systemInfoServiceMax.getSystemInfo();
    }

    @GetMapping(value = "/translate/{word}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> translateWord(@PathVariable String word) {
        System.out.println("[DEBUG] Petición recibida en /translate con palabra: " + word);

        try {
            WordResponse response = translationService.translateWord(word);
            System.out.println("[DEBUG] Respuesta generada correctamente: " + response);
            return ResponseEntity.ok(response);

        } catch (TranslationException e) {
            System.out.println("[ERROR] Fallo en traducción: " + e.getMessage());
            return ResponseEntity.status(502).body(Map.of(
                "error", "Servicio de traducción no disponible",
                "message", e.getMessage()
            ));

        } catch (DictionaryException e) {
            System.out.println("[ERROR] Fallo en diccionario: " + e.getMessage());
            return ResponseEntity.status(404).body(Map.of(
                "error", "No se encontró información en el diccionario",
                "message", e.getMessage()
            ));

        } catch (Exception e) {
            System.out.println("[ERROR] Error inesperado: " + e.getMessage());
            e.printStackTrace(); // para ver el stack completo en consola
            return ResponseEntity.status(500).body(Map.of(
                "error", "Error interno del servidor",
                "message", e.getMessage()
            ));
        }
    }

}
