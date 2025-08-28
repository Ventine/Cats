package com.cats.Proyect_Cats.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.cats.Proyect_Cats.DTO.WordResponse;
import com.cats.Proyect_Cats.exception.DictionaryException;
import com.cats.Proyect_Cats.exception.TranslationException;

@Service
public class TranslationService {

    private final RestTemplate restTemplate = new RestTemplate();

    public WordResponse translateWord(String word) {
        System.out.println("[DEBUG] Palabra recibida para traducir: " + word);

        String translated = translate(word);
        System.out.println("[DEBUG] Traducción obtenida: " + translated);

        if (translated == null) {
            throw new TranslationException("No se pudo traducir la palabra: " + word);
        }

        String pronunciation = null;
        String example = null;

        try {
            String dictUrl = "https://api.dictionaryapi.dev/api/v2/entries/en/" + translated;
            System.out.println("[DEBUG] URL diccionario: " + dictUrl);

            ResponseEntity<List> dictResp = restTemplate.getForEntity(dictUrl, List.class);
            List<?> body = dictResp.getBody();
            System.out.println("[DEBUG] Respuesta diccionario: " + body);

            if (body != null && !body.isEmpty()) {
                Map<?, ?> entry = (Map<?, ?>) body.get(0);
                pronunciation = extractPronunciation(entry);
                System.out.println("[DEBUG] Pronunciación extraída: " + pronunciation);

                example = extractExample(entry);
                System.out.println("[DEBUG] Ejemplo extraído: " + example);
            }
        } catch (Exception e) {
            System.out.println("[ERROR] Fallo al obtener datos del diccionario: " + e.getMessage());
            throw new DictionaryException("No se pudo obtener información del diccionario para: " + translated, e);
        }

        WordResponse response = new WordResponse(word, translated, pronunciation, example);
        System.out.println("[DEBUG] Respuesta final construida: " + response);
        return response;
    }

    private String translate(String word) {
        String translateUrl = "https://translate.argosopentech.com/translate";
        Map<String, Object> request = Map.of(
            "q", word, "source", "es", "target", "en", "format", "text"
        );
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, createJsonHeaders());

        System.out.println("[DEBUG] Request de traducción: " + request);

        try {
            Map<String, Object> resp = restTemplate.postForObject(translateUrl, entity, Map.class);
            System.out.println("[DEBUG] Respuesta de servicio de traducción: " + resp);
            return resp != null ? (String) resp.get("translatedText") : null;
        } catch (Exception e) {
            System.out.println("[ERROR] Fallo en servicio de traducción: " + e.getMessage());
            throw new TranslationException("Error llamando al servicio de traducción", e);
        }
    }

    private HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private String extractPronunciation(Map<?, ?> entry) {
        Object phoneticsObj = entry.get("phonetics");
        if (phoneticsObj instanceof List<?> phonetics) {
            for (Object ph : phonetics) {
                if (ph instanceof Map<?, ?> pm && pm.get("text") instanceof String txt) {
                    return txt;
                }
            }
        }
        return null;
    }

    private String extractExample(Map<?, ?> entry) {
        Object meaningsObj = entry.get("meanings");
        if (meaningsObj instanceof List<?> meanings) {
            for (Object m : meanings) {
                if (m instanceof Map<?, ?> mm) {
                    Object defsObj = mm.get("definitions");
                    if (defsObj instanceof List<?> defs) {
                        for (Object d : defs) {
                            if (d instanceof Map<?, ?> dm && dm.get("example") instanceof String ex) {
                                return ex;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
