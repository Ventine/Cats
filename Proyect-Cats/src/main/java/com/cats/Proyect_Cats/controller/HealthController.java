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
    public ResponseEntity<Map<String, Object>> getWeather(@PathVariable String city) {
        try {
            Map<String, Object> weatherData = weatherService.getWeather(city);

            if (weatherData == null || weatherData.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "No se encontraron datos para la ciudad: " + city));
            }

            return ResponseEntity.ok(weatherData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener el clima", "details", e.getMessage()));
        }
    }

    @GetMapping("/catfact")
    public ResponseEntity<Map<String, Object>> getCatFact() {
        try {
            String fact = catFactsService.getRandomFact();
            return ResponseEntity.ok(Map.of("fact", fact));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener cat fact", "details", e.getMessage()));
        }
    }

    @GetMapping("/joke")
    public ResponseEntity<Map<String, Object>> getJoke() {
        try {
            String joke = jokesService.getRandomJoke();
            return ResponseEntity.ok(Map.of("joke", joke));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener chiste", "details", e.getMessage()));
        }
    }

    @GetMapping("/systemInfo")
    public ResponseEntity<Map<String, Object>> getSystemInfo() {
        try {
            return ResponseEntity.ok(systemInfoService.getSystemInfo());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener información del sistema", "details", e.getMessage()));
        }
    }

    @GetMapping("/systemInfoMax")
    public ResponseEntity<Map<String, Object>> getSystemInfoMax() {
        try {
            return ResponseEntity.ok(systemInfoServiceMax.getSystemInfo());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener información del sistema (Max)", "details", e.getMessage()));
        }
    }

    @GetMapping(value = "/dictionaryEnglish/{word}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> translateWord(@PathVariable String word) {
        try {
            WordResponse response = translationService.translateWord(word);
            return ResponseEntity.ok(response);

        } catch (DictionaryException e) {
            return ResponseEntity.status(404).body(Map.of(
                "error", "No se encontró información en el diccionario",
                "message", e.getMessage()
            ));

        } catch (Exception e) {
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
    public ResponseEntity<?> reverseGeocode(
            @RequestParam double lat,
            @RequestParam double lon) {

        try {
            // Validación básica de coordenadas
            if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Coordenadas inválidas",
                                    "details", "Latitud debe estar entre -90 y 90, longitud entre -180 y 180"));
            }

            LocationResponse location = geoService.getLocation(lat, lon);

            if (location == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "No se pudo determinar la ubicación",
                                    "details", "Revisa las coordenadas proporcionadas"));
            }

            return ResponseEntity.ok(location);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener la ubicación",
                                "details", e.getMessage()));
        }
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
    public ResponseEntity<?> getCryptos() {
        try {
            List<CryptoResponseDto> cryptos = cryptoService.getTopCryptos();

            if (cryptos == null || cryptos.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "No se encontraron criptomonedas"));
            }

            return ResponseEntity.ok(cryptos);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener criptomonedas", 
                                "details", e.getMessage()));
        }
    }


}
