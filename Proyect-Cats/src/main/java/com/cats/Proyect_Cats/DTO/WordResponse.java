package com.cats.Proyect_Cats.DTO;
import lombok.Getter;
import lombok.Setter;

    @Getter
    @Setter
public class WordResponse {
    private String original;
    private String pronunciation;
    private String audio;
    private String definition;
    private String example;
    private String partOfSpeech;

    public WordResponse(String original, String pronunciation, String audio,
                        String definition, String example, String partOfSpeech) {
        this.original = original;
        this.pronunciation = pronunciation;
        this.audio = audio;
        this.definition = definition;
        this.example = example;
        this.partOfSpeech = partOfSpeech;
    }

    // getters y setters
}
