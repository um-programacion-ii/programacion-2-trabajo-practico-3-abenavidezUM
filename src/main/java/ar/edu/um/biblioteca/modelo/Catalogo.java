package ar.edu.um.biblioteca.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que gestiona la colección de libros en la biblioteca
 */
public class Catalogo {
    private List<Libro> libros;

    /**
     * Constructor que inicializa un catálogo vacío
     */
    public Catalogo() {
        this.libros = new ArrayList<>();
    }

    /**
     * Agrega un libro al catálogo
     * @param libro El libro a agregar
     * @return true si se agregó correctamente, false si ya existía un libro con el mismo ISBN
     */
    public boolean agregarLibro(Libro libro) {
        // Verificar que el libro no sea nulo
        if (libro == null) {
            return false;
        }
        
        // Verificar si ya existe un libro con el mismo ISBN
        for (Libro l : libros) {
            if (l.getIsbn().equals(libro.getIsbn())) {
                return false;
            }
        }
        
        return libros.add(libro);
    }

    /**
     * Busca un libro por su ISBN
     * @param isbn El ISBN del libro a buscar
     * @return El libro encontrado o null si no existe
     */
    public Libro buscarPorIsbn(String isbn) {
        // Verificar que el ISBN no sea nulo o vacío
        if (isbn == null || isbn.trim().isEmpty()) {
            return null;
        }
        
        for (Libro libro : libros) {
            if (libro.getIsbn().equals(isbn)) {
                return libro;
            }
        }
        return null;
    }

    /**
     * Obtiene todos los libros del catálogo
     * @return Lista con todos los libros
     */
    public List<Libro> obtenerTodosLosLibros() {
        return new ArrayList<>(libros);
    }

    /**
     * Obtiene todos los libros en estado DISPONIBLE
     * @return Lista con los libros disponibles
     */
    public List<Libro> obtenerLibrosDisponibles() {
        List<Libro> disponibles = new ArrayList<>();
        
        for (Libro libro : libros) {
            if (libro.getEstado() == Estado.DISPONIBLE) {
                disponibles.add(libro);
            }
        }
        
        return disponibles;
    }
    
    /**
     * Obtiene todos los libros en estado PRESTADO
     * @return Lista con los libros prestados
     */
    public List<Libro> obtenerLibrosPrestados() {
        List<Libro> prestados = new ArrayList<>();
        
        for (Libro libro : libros) {
            if (libro.getEstado() == Estado.PRESTADO) {
                prestados.add(libro);
            }
        }
        
        return prestados;
    }
    
    /**
     * Elimina un libro del catálogo por su ISBN
     * @param isbn El ISBN del libro a eliminar
     * @return true si se eliminó correctamente, false si no se encontró el libro
     */
    public boolean eliminarLibro(String isbn) {
        Libro libro = buscarPorIsbn(isbn);
        if (libro != null) {
            return libros.remove(libro);
        }
        return false;
    }
    
    /**
     * Busca libros por autor
     * @param autor El autor a buscar
     * @return Lista de libros del autor especificado
     */
    public List<Libro> buscarPorAutor(String autor) {
        List<Libro> resultado = new ArrayList<>();
        
        // Si el autor es nulo o vacío, retornar lista vacía
        if (autor == null || autor.trim().isEmpty()) {
            return resultado;
        }
        
        for (Libro libro : libros) {
            if (libro.getAutor().toLowerCase().contains(autor.toLowerCase())) {
                resultado.add(libro);
            }
        }
        
        return resultado;
    }
    
    /**
     * Busca libros por título
     * @param titulo El título a buscar
     * @return Lista de libros que contienen el título especificado
     */
    public List<Libro> buscarPorTitulo(String titulo) {
        List<Libro> resultado = new ArrayList<>();
        
        if (titulo == null || titulo.trim().isEmpty()) {
            return resultado;
        }
        
        for (Libro libro : libros) {
            if (libro.getTitulo().toLowerCase().contains(titulo.toLowerCase())) {
                resultado.add(libro);
            }
        }
        
        return resultado;
    }
    
    /**
     * Obtiene la cantidad total de libros en el catálogo
     * @return Número de libros en el catálogo
     */
    public int contarLibros() {
        return libros.size();
    }
    
    /**
     * Cuenta la cantidad de libros disponibles
     * @return Número de libros disponibles
     */
    public int contarLibrosDisponibles() {
        return obtenerLibrosDisponibles().size();
    }
    
    /**
     * Cuenta la cantidad de libros prestados
     * @return Número de libros prestados
     */
    public int contarLibrosPrestados() {
        return obtenerLibrosPrestados().size();
    }
} 