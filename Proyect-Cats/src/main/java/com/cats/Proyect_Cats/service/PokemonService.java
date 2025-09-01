package com.cats.Proyect_Cats.service;

import com.cats.Proyect_Cats.client.PokeApiClient;
import com.cats.Proyect_Cats.DTO.PokemonResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PokemonService {

    private final PokeApiClient client;
    private final ObjectMapper mapper;

    public PokemonService(PokeApiClient client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    public Mono<PokemonResponse> getPokemon(String name) {
        return client.getPokemonRaw(name)
                .map(this::mapToResponse);
    }

    private PokemonResponse mapToResponse(String json) {
        try {
            JsonNode root = mapper.readTree(json);

            PokemonResponse response = new PokemonResponse();
            response.setName(root.get("name").asText());
            response.setHeight(root.get("height").asInt());
            response.setWeight(root.get("weight").asInt());

            List<String> abilities = StreamSupport.stream(
                    root.get("abilities").spliterator(), false)
                    .map(a -> a.get("ability").get("name").asText())
                    .collect(Collectors.toList());

            List<String> types = StreamSupport.stream(
                    root.get("types").spliterator(), false)
                    .map(t -> t.get("type").get("name").asText())
                    .collect(Collectors.toList());

            String spriteUrl = root.get("sprites").get("front_default").asText();

            response.setAbilities(abilities);
            response.setTypes(types);
            response.setSpriteUrl(spriteUrl);

            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error procesando datos del Pokémon", e);
        }
    }
}
