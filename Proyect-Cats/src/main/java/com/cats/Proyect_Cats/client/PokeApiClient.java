package com.cats.Proyect_Cats.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Component
public class PokeApiClient {

    private final WebClient webClient;

    public PokeApiClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("https://pokeapi.co/api/v2").build();
    }

    public Mono<String> getPokemonRaw(String name) {
        return webClient.get()
                .uri("/pokemon/{name}", name.toLowerCase())
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), resp -> 
                    Mono.error(new RuntimeException("Pokémon no encontrado: " + name)))
                .onStatus(status -> status.is5xxServerError(), resp -> 
                    Mono.error(new RuntimeException("Error en PokeAPI")))
                .bodyToMono(String.class);
    }
}
