package com.cats.Proyect_Cats.exception;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cats.Proyect_Cats.DTO.BoredActivityResponse;

// Servicio: capa intermedia (lógica de negocio y consumo externo)
@Service
public class BoredApiService {

    private final RestTemplate restTemplate;

    // Construimos el RestTemplate a través del builder (Spring Boot recomienda esto)
    public BoredApiService(RestTemplateBuilder builder) {
        this.restTemplate = builder.build();
    }

    private static final String BORED_API_URL = "https://bored-api.appbrewery.com/random";

    /**
     * Consume la Bored API y devuelve un objeto de tipo BoredActivityResponse.
     */
    public BoredActivityResponse fetchActivity() {
        try {
            ResponseEntity<BoredActivityResponse> response =
                    restTemplate.getForEntity(BORED_API_URL, BoredActivityResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Respuesta inesperada del servidor externo");
            }
        } catch (Exception ex) {
            // Encapsulamos cualquier error de conexión/parsing
            throw new RuntimeException("Error al consumir la Bored API: " + ex.getMessage(), ex);
        }
    }
}
