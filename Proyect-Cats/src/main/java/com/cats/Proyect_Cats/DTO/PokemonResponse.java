package com.cats.Proyect_Cats.DTO;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class PokemonResponse {
    private String name;
    private int height;
    private int weight;
    private List<String> abilities;
    private List<String> types;
    private String spriteUrl;
}
