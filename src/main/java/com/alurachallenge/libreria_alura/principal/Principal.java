package com.alurachallenge.libreria_alura.principal;

import com.alurachallenge.libreria_alura.model.*;
import com.alurachallenge.libreria_alura.repository.AutorRepository;
import com.alurachallenge.libreria_alura.repository.LibroRepository;
import com.alurachallenge.libreria_alura.service.ConsumoAPI;
import com.alurachallenge.libreria_alura.service.ConvierteDatos;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.*;
import java.util.stream.Collectors;


public class Principal {

    Scanner leer = new Scanner(System.in);
    private final String URL_BASE = "https://gutendex.com/books/?search=";

    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();

    private LibroRepository libroRepository;
    private AutorRepository autorRepository;

    private List<Libro> libros;
    private List<Autor> autores;

    //constructor que contiene el repositorio
    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void muestraMenu() {
        var opc = 0;

        do {
            System.out.println("""

                    MENU OPCIONES:
                    1 - Buscar Libros.
                    2 - Listar Libros registrados.
                    3 - Listar Autores registrados.
                    4 - Listar Autores por año.
                    5 - Listar Libros por idioma.
                    6 - Mostrar Estadisticas libros.

                    0 - Salir.
                    
                    """);

            opc = leer.nextInt();

            switch (opc) {
                case 1:
                    buscarLibroWeb();
                    break;

                case 2:
                    listarLibros();
                    break;

                case 3:
                    listarAutores();
                    break;

                case 4:
                    listarAutoresPorAño();
                    break;

                case 5:
                    listarLibrosPorIdioma();
                    break;

                case 6:
                    librosEstadisticas();
                    break;


                case 0:
                    System.out.println("Saliendo!!");
                    break;
            }

        } while (opc != 0);
    }

    private void librosEstadisticas() {
        // trae los libros de la base de datos
        libros = libroRepository.findAll();

        DoubleSummaryStatistics est = libros.stream()
                .filter(l -> l.getDescargas() > 0)
                .collect(Collectors.summarizingDouble(Libro::getDescargas));
        System.out.println("Media de descargas: " + est.getAverage() +
                "\nLibro con más descargas: " + est.getMax() + "\nLibro con menos descargas: " + est.getMin());
    }

    private void listarLibrosPorIdioma() {
        leer.nextLine();
        System.out.println("Ingrese el idioma del libro a buscar: " +
                "\ningles: en \nespanol: es \nfrances: fr \nitaliano: it \nportugues: pt ");
        var lenguaje = leer.nextLine();
        var idioma = Idioma.fromString(lenguaje);

        List<Libro> libroPorIdioma = libroRepository.findByLenguaje(idioma);
        System.out.println("libros de idioma: " + idioma);
        libroPorIdioma.forEach(System.out::println);
    }

    private void listarAutoresPorAño() {
        leer.nextLine();
        System.out.println("ingrese el año a buscar: ");
        var year = leer.nextInt();
        leer.nextLine();
        autores = autorRepository.autoresPorAñoVivos(year);

        autores.stream()
                .forEach(a -> System.out.println("Fecha nacimiento: " + a.getFechaNacimiento() + ", autor: " + a.getNombre()));
    }

    private void listarAutores() {
        autores = autorRepository.findAll();
        autores.stream()
                .forEach(System.out::println);
    }

    private void listarLibros() {
        libros = libroRepository.findAll();
        libros.stream()
                .forEach(System.out::println);
    }

    private DatosLibros getDatosLibros() {
        leer.nextLine();
        System.out.println("1. Ingrese el nombre del libro: ");
        var nombreLibro = leer.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE + nombreLibro.replace(" ", "+"));
        System.out.println(json);
        var datos = conversor.obtenerDatos(json, Datos.class);
        Optional<DatosLibros> libroBuscado = datos.libros().stream()
                .filter(l -> l.titulo().toUpperCase().contains(nombreLibro.toUpperCase()))
                .findFirst();

        if (libroBuscado.isPresent()){
            System.out.println("Libro encontrado...");
            return libroBuscado.get();
        } else {
            System.out.println("libro no encontrado, intenta con otro título\n");
            return null;
        }
    }

    private void buscarLibroWeb() {
        Optional<DatosLibros> datosOpcional = Optional.ofNullable(getDatosLibros());

        if(datosOpcional.isPresent()) {
            DatosLibros datos = datosOpcional.get();
            Libro libro = new Libro(datos);

            if (!datos.autores().isEmpty()) {
                DatosAutores datosAutor = datos.autores().get(0);
                // Buscar si el autor ya existe en la base de datos
                Optional<Autor> autorExistente = autorRepository.findByNombre(datosAutor.nombre());

                Autor autor;
                if (autorExistente.isPresent()) {
                    // Si el autor ya existe, usar el autor existente
                    autor = autorExistente.get();
                } else {
                    // Si el autor no existe, crear uno nuevo y guardarlo
                    autor = new Autor(datosAutor);
                    autorRepository.save(autor);
                }
                libro.setAutor(autor); // Asignar el autor al libro
            }

            try {
                libroRepository.save(libro); // Guardar el libro
                System.out.println(libro.getTitulo() + " guardado exitosamente!!!");
            } catch (DataIntegrityViolationException e) {
                System.out.println("Error: libro ya está almacenado en la base de datos, intenta con otro libro.\n");
            }
        }
    }

}

//________________________________________________________________________________________________________________
//
//        //pasar todos los datos a una lista
//        System.out.println("\nlista de 10 libros: ");
//        List<Datos> listaLibrosList = new ArrayList<Datos>();
//        for (int i = 1; i < 11; i++) {
//            json = consumoAPI.obtenerDatos(URL_BASE + "?ids=" + i);
//            var datosListaLibros = conversor.obtenerDatos(json, Datos.class);
//            listaLibrosList.add(datosListaLibros);
//        }
//        listaLibrosList.forEach(l -> System.out.println(l.libros()));
//
//        //pasando la informacion de los libros en una lista mutable
//        List<DatosLibros> datosLibros = listaLibrosList.stream()
//                .flatMap(l -> l.libros().stream())
//                .collect(Collectors.toList());
//
//
//        //top 5 con mas descargas
//        System.out.println("\ntop 5 libros con mas descargas: ");
//        datosLibros.stream()
//                .sorted(Comparator.comparing(DatosLibros::descargas).reversed())
//                .forEach(System.out::println);
//
//        datos.libros().stream()
//                .sorted(Comparator.comparing(DatosLibros::descargas).reversed())
//                .limit(10)
//                .map(l ->l.titulo().toUpperCase() + " Descargas: " + l.descargas()) //se mapean los libros imprimiendo solo el titulo en mayusculas
//                .forEach(System.out::println);
//
//        //buscar libro por nombre:
//        System.out.println("Ingrese el nombre del libro que desea buscar: ");
//        String nombre = leer.nextLine();
//        Optional<DatosLibros> bookFound = datos.libros().stream()
//                .filter(l -> l.titulo().toUpperCase().contains(nombre.toUpperCase()))
//                .findFirst();
//        if (bookFound.isPresent()) {
//            System.out.println("Libro encontrado: " +
//                    "\n" + "Id: " + bookFound.get().id() +
//                    "\n" + "Titulo: " + bookFound.get().titulo() +
//                    "\n" + bookFound.get().autores());
//        }else{
//            System.out.println("El libro no existe");
//        }
//
//        //otra forma de imprimir pero como string
//        Optional<String> bookFounded = datos.libros().stream()
//                .filter(l -> l.titulo().toUpperCase().contains(nombre.toUpperCase()))
//                .map(l -> l.id() + " " + l.titulo() + " " + l.autores() + " " + l.lenguajes())
//                .findFirst();
//
//        if (bookFounded.isPresent()) {
//            System.out.println(bookFounded.get());
//        }
//
//        //buscar libro en varios idiomas:
//        System.out.println("write the name of the book: ");
//        var nombreLibro = leer.nextLine();
//        json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + nombreLibro.replace(" ", "+"));
//        var datosBusqueda = conversor.obtenerDatos(json, Datos.class);
//        Optional<DatosLibros> libroEncontrado = datosBusqueda.libros().stream()
//                .filter(l -> l.titulo().toUpperCase().contains(nombre.toUpperCase()))
//                .findFirst();
//        if (libroEncontrado.isPresent()) {
//            System.out.println("Libro encontrado: " +
//                    "\n" + "Id: " + libroEncontrado.get().id() +
//                    "\n" + "Titulo: " + libroEncontrado.get().titulo() +
//                    "\n" + libroEncontrado.get().autores());
//        }else{
//            System.out.println("El libro no existe");
//        }
//
//        //Estadisticas de los libros
//        IntSummaryStatistics est = datos.libros().parallelStream()
//                .filter(l ->l.descargas() > 0)
//                .collect(Collectors.summarizingInt(DatosLibros::descargas));
//        System.out.println("Libro con mas descargas: " + est.getMax());
//        System.out.println("Libro con menos descargas: " + est.getMin());
//        System.out.println("Libros analizados: " + est.getCount());
//}
