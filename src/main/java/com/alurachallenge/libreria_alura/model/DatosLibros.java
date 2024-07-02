package com.alurachallenge.libreria_alura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosLibros(
        Integer id,
        @JsonAlias("title") String titulo,
        @JsonAlias("subjects") List<String> subjects,
        @JsonAlias("authors") List<DatosAutores> autores,
        @JsonAlias("bookshelves") List<String> estanterias,
        @JsonAlias("languages") List<String> lenguajes,
        Boolean copyright,
        String media_type,
        @JsonAlias("download_count") Integer descargas
        ) { }
