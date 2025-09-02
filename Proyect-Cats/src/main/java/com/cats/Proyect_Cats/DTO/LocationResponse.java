package com.cats.Proyect_Cats.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationResponse {
    private String display_name;
    private Address address;

    @Getter
    @Setter
    public static class Address {
        private String road;
        private String city;
        private String country;
    }
}
