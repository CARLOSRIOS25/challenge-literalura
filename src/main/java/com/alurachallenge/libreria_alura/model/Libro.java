package com.alurachallenge.libreria_alura.model;

import jakarta.persistence.*;
import java.util.stream.Collectors;

@Entity
@Table(name = "libros")
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique=true)
    private String titulo;
    @Enumerated(EnumType.STRING)
    private Idioma lenguaje;
    private Integer descargas;
    @ManyToOne
    private Autor autor;

    public Libro() {
    }

    public Libro(DatosLibros datosLibros) {
        this.titulo = datosLibros.titulo();
        if (!datosLibros.autores().isEmpty()) {
            this.autor = new Autor(datosLibros.autores().get(0));
        }
        this.lenguaje = Idioma.fromString(datosLibros.lenguajes().stream().limit(1).collect(Collectors.joining()));
        this.descargas = datosLibros.descargas();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Idioma getLenguaje() {
        return lenguaje;
    }

    public void setLenguaje(Idioma lenguaje) {
        this.lenguaje = lenguaje;
    }

    public Integer getDescargas() {
        return descargas;
    }

    public void setDescargas(Integer descargas) {
        this.descargas = descargas;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    @Override
    public String toString() {
        String autorInfo = (autor != null) ? ", autor= (" + autor.getNombre() + ")" : ", autor= (Sin autor)";
        return  "titulo='" + titulo + '\'' +
                autorInfo +
                ", lenguajes=" + lenguaje +
                ", descargas=" + descargas;
    }
}
