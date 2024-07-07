package com.alurachallenge.libreria_alura.repository;

import com.alurachallenge.libreria_alura.model.Idioma;
import com.alurachallenge.libreria_alura.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Long> {

    List<Libro> findByLenguaje(Idioma idioma);
}
