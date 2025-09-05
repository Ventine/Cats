package com.cats.Proyect_Cats.DTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// DTO: clase para mapear la respuesta JSON de la Bored API
public class BoredActivityResponse {
    private String activity;
    private double availability;
    private String type;
    private int participants;
    private double price;
    private String accessibility;  // <-- ahora es String
    private String duration;
    private boolean kidFriendly;
    private String link;
    private String key;
}
