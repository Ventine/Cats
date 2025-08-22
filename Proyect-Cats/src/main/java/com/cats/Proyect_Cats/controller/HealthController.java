package com.cats.Proyect_Cats.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

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
}
