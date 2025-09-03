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
        System.out.println("[DEBUG] Palabra recibida para diccionario: " + word);

        try {
            String dictUrl = "https://api.dictionaryapi.dev/api/v2/entries/en/" + word;
            System.out.println("[DEBUG] URL diccionario: " + dictUrl);

            ResponseEntity<List> dictResp = restTemplate.getForEntity(dictUrl, List.class);
            List<?> body = dictResp.getBody();
            System.out.println("[DEBUG] Respuesta diccionario: " + body);

            if (body == null || body.isEmpty()) {
                throw new DictionaryException("No se encontró información en el diccionario para: " + word);
            }

            Map<?, ?> entry = (Map<?, ?>) body.get(0);

            String pronunciation = extractPronunciation(entry);
            String audio = extractAudio(entry);
            String definition = extractDefinition(entry);
            String example = extractExample(entry);
            String partOfSpeech = extractPartOfSpeech(entry);

            WordResponse response = new WordResponse(word, pronunciation, audio, definition, example, partOfSpeech);
            System.out.println("[DEBUG] Respuesta final construida: " + response);
            return response;

        } catch (Exception e) {
            throw new DictionaryException("Error consumiendo el diccionario para: " + word, e);
        }
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

    private String extractAudio(Map<?, ?> entry) {
        Object phoneticsObj = entry.get("phonetics");
        if (phoneticsObj instanceof List<?> phonetics) {
            for (Object ph : phonetics) {
                if (ph instanceof Map<?, ?> pm && pm.get("audio") instanceof String audio && !audio.isEmpty()) {
                    return audio;
                }
            }
        }
        return null;
    }

    private String extractDefinition(Map<?, ?> entry) {
        Object meaningsObj = entry.get("meanings");
        if (meaningsObj instanceof List<?> meanings) {
            for (Object m : meanings) {
                if (m instanceof Map<?, ?> mm) {
                    Object defsObj = mm.get("definitions");
                    if (defsObj instanceof List<?> defs) {
                        for (Object d : defs) {
                            if (d instanceof Map<?, ?> dm && dm.get("definition") instanceof String def) {
                                return def;
                            }
                        }
                    }
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

    private String extractPartOfSpeech(Map<?, ?> entry) {
        Object meaningsObj = entry.get("meanings");
        if (meaningsObj instanceof List<?> meanings && !meanings.isEmpty()) {
            Object first = meanings.get(0);
            if (first instanceof Map<?, ?> mm && mm.get("partOfSpeech") instanceof String pos) {
                return pos;
            }
        }
        return null;
    }
}
