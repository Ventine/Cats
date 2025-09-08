package com.cats.Proyect_Cats.controller;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cats.Proyect_Cats.service.CatFactsService;
import com.cats.Proyect_Cats.service.CryptoService;
import com.cats.Proyect_Cats.service.GeoService;
import com.cats.Proyect_Cats.service.JokesService;
import com.cats.Proyect_Cats.service.PokemonService;
import com.cats.Proyect_Cats.service.SystemInfoService;
import com.cats.Proyect_Cats.service.SystemInfoServiceMax;
import com.cats.Proyect_Cats.service.WeatherService;

import reactor.core.publisher.Mono;

import com.cats.Proyect_Cats.service.TranslationService;
import com.cats.Proyect_Cats.DTO.BoredActivityResponse;
import com.cats.Proyect_Cats.DTO.CryptoResponseDto;
import com.cats.Proyect_Cats.DTO.LocationResponse;
import com.cats.Proyect_Cats.DTO.PokemonResponse;
import com.cats.Proyect_Cats.DTO.WordResponse;
import com.cats.Proyect_Cats.exception.BoredApiService;
import com.cats.Proyect_Cats.exception.DictionaryException;

@RestController
public class HealthController {

    private final WeatherService weatherService;
    private final CatFactsService catFactsService;
    private final JokesService jokesService;
    private final SystemInfoService systemInfoService;
    private final SystemInfoServiceMax systemInfoServiceMax;
    private final TranslationService translationService;
    private final PokemonService pokeservice;
    private final GeoService geoService;
    private final BoredApiService boredApiService;
    private final CryptoService  cryptoService ;

    public HealthController(WeatherService weatherService, CatFactsService catFactsService, 
    JokesService jokesService, SystemInfoService systemInfoService, SystemInfoServiceMax systemInfoServiceMax,
    TranslationService translationService, PokemonService pokeservice, GeoService geoService,
    BoredApiService boredApiService, CryptoService cryptoService) {
        this.weatherService = weatherService;
        this.catFactsService = catFactsService;
        this.jokesService = jokesService;
        this.systemInfoService = systemInfoService;
        this.systemInfoServiceMax = systemInfoServiceMax;
        this.translationService = translationService;
        this.pokeservice = pokeservice;
        this.geoService = geoService;
        this.boredApiService = boredApiService;
        this.cryptoService = cryptoService;
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

    @GetMapping(value = "/dictionaryEnglish/{word}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> translateWord(@PathVariable String word) {
        System.out.println("[DEBUG] Petición recibida en /translate con palabra: " + word);

        try {
            WordResponse response = translationService.translateWord(word);
            System.out.println("[DEBUG] Respuesta generada correctamente: " + response);
            return ResponseEntity.ok(response);

        } catch (DictionaryException e) {
            System.out.println("[ERROR] Fallo en diccionario: " + e.getMessage());
            return ResponseEntity.status(404).body(Map.of(
                "error", "No se encontró información en el diccionario",
                "message", e.getMessage()
            ));

        } catch (Exception e) {
            System.out.println("[ERROR] Error inesperado: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                "error", "Error interno del servidor",
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("PokeDex/{name}")
    public Mono<ResponseEntity<PokemonResponse>> getPokemon(@PathVariable String name) {
        return pokeservice.getPokemon(name)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    @GetMapping("/geo")
    public LocationResponse reverseGeocode(
            @RequestParam double lat,
            @RequestParam double lon) {
        return geoService.getLocation(lat, lon);
    }

     @GetMapping("/activity")
    public ResponseEntity<?> getRandomActivity() {
        try {
            // Llamamos al servicio que consume la API
            BoredActivityResponse response = boredApiService.fetchActivity();
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            // Si algo falla, devolvemos un error controlado
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of(
                            "error", "No se pudo obtener la actividad",
                            "detalle", ex.getMessage()
                    ));
        }
    }

    @GetMapping("/crypto")
    public List<CryptoResponseDto> getCryptos() {
        return cryptoService.getTopCryptos();
    }

}
