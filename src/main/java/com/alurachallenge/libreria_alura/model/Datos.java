package com.alurachallenge.libreria_alura.model;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

public record Datos(
        @JsonAlias("count") Integer cuenta,
        @JsonAlias("next") String siguente,
        @JsonAlias("previous") String anterior,
        @JsonAlias("results") List<DatosLibros> libros
) {
}
