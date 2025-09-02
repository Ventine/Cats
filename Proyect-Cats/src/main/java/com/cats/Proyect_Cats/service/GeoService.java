package com.cats.Proyect_Cats.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cats.Proyect_Cats.DTO.LocationResponse;

@Service
public class GeoService {

    private final RestTemplate restTemplate = new RestTemplate();

    public LocationResponse getLocation(double lat, double lon) {
        String url = "https://nominatim.openstreetmap.org/reverse?format=json&lat=" 
                     + lat + "&lon=" + lon + "&zoom=18&addressdetails=1";
        return restTemplate.getForObject(url, LocationResponse.class);
    }
}
