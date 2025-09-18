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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Health Controller", description = "Servicios de salud, clima, chistes, criptos y más")
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

    /**
     * Verifica el estado de la aplicación.
     *
     * @return información de estado, host, ip, versión y sistema operativo
     * @throws UnknownHostException si no se puede resolver el host
     */
    @Operation(summary = "Verificar estado de la aplicación", description = "Devuelve información básica del sistema y estado del servicio")
    @ApiResponse(responseCode = "200", description = "Servicio en línea")
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

    /**
     * Obtiene el clima de una ciudad.
     *
     * @param city nombre de la ciudad
     * @return datos climáticos en formato JSON
     */
    @Operation(summary = "Clima de una ciudad", description = "Obtiene información del clima en la ciudad indicada")
    @ApiResponse(responseCode = "200", description = "Datos climáticos encontrados")
    @ApiResponse(responseCode = "404", description = "No se encontraron datos para la ciudad")
    @GetMapping("/weather/{city}")    public ResponseEntity<Map<String, Object>> getWeather(@PathVariable String city) {
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

    /**
     * Obtiene un dato curioso de gatos.
     *
     * @return fact aleatorio
     */
    @Operation(summary = "Dato curioso de gatos", description = "Devuelve un dato aleatorio sobre gatos")
    @ApiResponse(responseCode = "200", description = "Dato encontrado")
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

    /**
     * Devuelve un chiste aleatorio.
     *
     * @return chiste en formato JSON
     */
    @Operation(summary = "Chiste aleatorio", description = "Devuelve un chiste random")
    @ApiResponse(responseCode = "200", description = "Chiste encontrado")
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

    /**
     * Devuelve información básica del sistema (CPU, memoria, etc).
     */
    @Operation(summary = "Información del sistema", description = "Devuelve datos básicos del sistema")
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
    /**
     * Devuelve información extendida del sistema.
     */
    @Operation(summary = "Información extendida del sistema", description = "Devuelve métricas más detalladas del sistema")
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

    /**
     * Traduce una palabra en inglés.
     *
     * @param word palabra a traducir
     */
    @Operation(summary = "Diccionario Inglés", description = "Devuelve significado y traducción de una palabra en inglés")
    @ApiResponse(responseCode = "404", description = "Palabra no encontrada en el diccionario")
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

    /**
     * Devuelve un Pokémon de la Pokédex.
     *
     * @param name nombre del Pokémon
     */
    @Operation(summary = "Pokédex", description = "Devuelve información de un Pokémon por nombre")
    @ApiResponse(responseCode = "200", description = "Pokémon encontrado")
    @ApiResponse(responseCode = "400", description = "Error al consultar el Pokémon")
    @GetMapping("PokeDex/{name}")
    public Mono<ResponseEntity<PokemonResponse>> getPokemon(@PathVariable String name) {
        return pokeservice.getPokemon(name)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }

    /**
     * Obtiene información de ubicación a partir de coordenadas.
     *
     * @param lat latitud (-90 a 90)
     * @param lon longitud (-180 a 180)
     */
    @Operation(summary = "Geolocalización", description = "Devuelve ciudad y país según coordenadas")
    @ApiResponse(responseCode = "200", description = "Ubicación encontrada")
    @ApiResponse(responseCode = "400", description = "Coordenadas inválidas")
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

    /**
     * Devuelve una actividad aleatoria (Bored API).
     */
    @Operation(summary = "Actividad random", description = "Sugiere una actividad aleatoria para hacer")
    @ApiResponse(responseCode = "200", description = "Actividad encontrada")
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

    /**
     * Devuelve el top de criptomonedas.
     */
    @Operation(summary = "Top criptomonedas", description = "Devuelve listado de criptomonedas populares")
    @ApiResponse(responseCode = "200", description = "Criptomonedas encontradas")
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
