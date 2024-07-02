package com.alurachallenge.libreria_alura.principal;

import com.alurachallenge.libreria_alura.model.DatosLibros;
import com.alurachallenge.libreria_alura.model.Datos;
import com.alurachallenge.libreria_alura.service.ConsumoAPI;
import com.alurachallenge.libreria_alura.service.ConvierteDatos;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    Scanner leer = new Scanner(System.in);

    private final String URL_BASE = "https://gutendex.com/books/";

    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();

    public void menu() {

        //transformando los datos a objetos de java
        System.out.println("\ndatos libros: ");
        var json = consumoAPI.obtenerDatos(URL_BASE);
        var datos = conversor.obtenerDatos(json, Datos.class);
        System.out.println(datos);

        //pasar todos los datos a una lista
//        System.out.println("\nlista de 10 libros: ");
//        List<Datos> listaLibrosList = new ArrayList<Datos>();
//        for (int i = 1; i < 11; i++) {
//            json = consumoAPI.obtenerDatos(URL_BASE + "?ids=" + i);
//            var datosListaLibros = conversor.obtenerDatos(json, Datos.class);
//            listaLibrosList.add(datosListaLibros);
//        }
//        listaLibrosList.forEach(l -> System.out.println(l.libros()));

        //pasando la informacion de los libros en una lista mutable
//        List<DatosLibros> datosLibros = listaLibrosList.stream()
//                .flatMap(l -> l.libros().stream())
//                .collect(Collectors.toList());


        //top 5 con mas descargas
        System.out.println("\ntop 5 libros con mas descargas: ");
//        datosLibros.stream()
//                .sorted(Comparator.comparing(DatosLibros::descargas).reversed())
//                .forEach(System.out::println);

        datos.libros().stream()
                .sorted(Comparator.comparing(DatosLibros::descargas).reversed())
                .limit(10)
                .map(l ->l.titulo().toUpperCase() + " Descargas: " + l.descargas()) //se mapean los libros imprimiendo solo el titulo en mayusculas
                .forEach(System.out::println);

        //buscar libro por nombre:
        System.out.println("Ingrese el nombre del libro que desea buscar: ");
        String nombre = leer.nextLine();
        Optional<DatosLibros> bookFound = datos.libros().stream()
                .filter(l -> l.titulo().toUpperCase().contains(nombre.toUpperCase()))
                .findFirst();
        if (bookFound.isPresent()) {
            System.out.println("Libro encontrado: " +
                    "\n" + "Id: " + bookFound.get().id() +
                    "\n" + "Titulo: " + bookFound.get().titulo() +
                    "\n" + bookFound.get().autores());
        }else{
            System.out.println("El libro no existe");
        }

        //otra forma de imprimir pero como string
//        Optional<String> bookFounded = datos.libros().stream()
//                .filter(l -> l.titulo().toUpperCase().contains(nombre.toUpperCase()))
//                .map(l -> l.id() + " " + l.titulo() + " " + l.autores() + " " + l.lenguajes())
//                .findFirst();
//
//        if (bookFounded.isPresent()) {
//            System.out.println(bookFounded.get());
//        }

        //buscar libro en varios idiomas:
        System.out.println("write the name of the book: ");
        var nombreLibro = leer.nextLine();
        json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + nombreLibro.replace(" ", "+"));
        var datosBusqueda = conversor.obtenerDatos(json, Datos.class);
        Optional<DatosLibros> libroEncontrado = datosBusqueda.libros().stream()
                .filter(l -> l.titulo().toUpperCase().contains(nombre.toUpperCase()))
                .findFirst();
        if (libroEncontrado.isPresent()) {
            System.out.println("Libro encontrado: " +
                    "\n" + "Id: " + libroEncontrado.get().id() +
                    "\n" + "Titulo: " + libroEncontrado.get().titulo() +
                    "\n" + libroEncontrado.get().autores());
        }else{
            System.out.println("El libro no existe");
        }

        //Estadisticas de los libros
        IntSummaryStatistics est = datos.libros().parallelStream()
                .filter(l ->l.descargas() > 0)
                .collect(Collectors.summarizingInt(DatosLibros::descargas));
        System.out.println("Libro con mas descargas: " + est.getMax());
        System.out.println("Libro con menos descargas: " + est.getMin());
        System.out.println("Libros analizados: " + est.getCount());

    }
}
