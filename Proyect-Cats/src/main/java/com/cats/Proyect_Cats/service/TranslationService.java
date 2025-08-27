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
        String translated = translate(word);
        if (translated == null) {
            throw new TranslationException("No se pudo traducir la palabra: " + word);
        }

        String pronunciation = null;
        String example = null;

        try {
            String dictUrl = "https://api.dictionaryapi.dev/api/v2/entries/en/" + translated;
            ResponseEntity<List> dictResp = restTemplate.getForEntity(dictUrl, List.class);
            List<?> body = dictResp.getBody();
            if (body != null && !body.isEmpty()) {
                Map<?, ?> entry = (Map<?, ?>) body.get(0);
                pronunciation = extractPronunciation(entry);
                example = extractExample(entry);
            }
        } catch (Exception e) {
            throw new DictionaryException("No se pudo obtener información del diccionario para: " + translated, e);
        }

        return new WordResponse(word, translated, pronunciation, example);
    }

    private String translate(String word) {
        String translateUrl = "https://translate.argosopentech.com/translate";
        Map<String, Object> request = Map.of(
            "q", word, "source", "es", "target", "en", "format", "text"
        );
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, createJsonHeaders());

        try {
            Map<String, Object> resp = restTemplate.postForObject(translateUrl, entity, Map.class);
            return resp != null ? (String) resp.get("translatedText") : null;
        } catch (Exception e) {
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
