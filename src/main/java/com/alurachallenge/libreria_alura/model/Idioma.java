package com.alurachallenge.libreria_alura.model;

public enum Idioma {
    ESPANOL("es"),
    INGLES("en"),
    FRANCES("fr"),
    ITALIANO("it"),
    PORTUGUES("pt");

    private String idioma;

    Idioma(String idioma) {
        this.idioma = idioma;
    }

    public static Idioma fromString(String text) {
        for (Idioma idioma : Idioma.values()) {
            if (idioma.idioma.equalsIgnoreCase(text)) {
                return idioma;
            }
        }
        throw new IllegalArgumentException("Ningun idioma encontrado: " + text);
    }
}
