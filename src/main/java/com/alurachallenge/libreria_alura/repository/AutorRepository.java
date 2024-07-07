package com.alurachallenge.libreria_alura.repository;

import com.alurachallenge.libreria_alura.model.Autor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AutorRepository extends JpaRepository<Autor, Long> {

    @Query("select a from Autor a where a.fechaNacimiento <= :year and a.fechaFallecimiento >= :year")
    List<Autor> autoresPorAñoVivos(int year);

    Optional<Autor> findByNombre(String nombre);
}
