package com.cats.Proyect_Cats.DTO;

public class WordResponse {
    private String original;
    private String translated;
    private String pronunciation;
    private String example;

    public WordResponse(String original, String translated, String pronunciation, String example) {
        this.original = original;
        this.translated = translated;
        this.pronunciation = pronunciation;
        this.example = example;
    }

    // getters y setters
}
