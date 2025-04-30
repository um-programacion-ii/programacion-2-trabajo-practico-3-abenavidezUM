package ar.edu.um.biblioteca.sistema;

import ar.edu.um.biblioteca.modelo.Catalogo;
import ar.edu.um.biblioteca.modelo.Estado;
import ar.edu.um.biblioteca.modelo.Libro;
import ar.edu.um.biblioteca.modelo.Prestamo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que gestiona los préstamos de libros en la biblioteca
 */
public class SistemaPrestamos {
    private Catalogo catalogo;
    private List<Prestamo> prestamosActivos;
    
    /**
     * Constructor que inicializa el sistema de préstamos con un catálogo
     * @param catalogo El catálogo de libros de la biblioteca
     */
    public SistemaPrestamos(Catalogo catalogo) {
        this.catalogo = catalogo;
        this.prestamosActivos = new ArrayList<>();
    }
    
    /**
     * Realiza el préstamo de un libro por su ISBN
     * @param isbn El ISBN del libro a prestar
     * @return El préstamo realizado o null si no se pudo realizar
     */
    public Prestamo prestarLibro(String isbn) {
        // Buscar el libro en el catálogo
        Libro libro = catalogo.buscarPorIsbn(isbn);
        
        // Verificar si el libro existe
        if (libro == null) {
            System.out.println("El libro con ISBN " + isbn + " no existe en el catálogo");
            return null;
        }
        
        // Verificar si el libro ya está prestado
        if (libro.getEstado() == Estado.PRESTADO) {
            System.out.println("El libro ya está prestado");
            return null;
        }
        
        // Crear el préstamo y agregarlo a la lista de activos
        Prestamo prestamo = new Prestamo(libro);
        prestamosActivos.add(prestamo);
        
        return prestamo;
    }
    
    /**
     * Realiza el préstamo de un libro por su ISBN con una duración específica
     * @param isbn El ISBN del libro a prestar
     * @param diasPrestamo El número de días del préstamo
     * @return El préstamo realizado o null si no se pudo realizar
     */
    public Prestamo prestarLibro(String isbn, int diasPrestamo) {
        // Buscar el libro en el catálogo
        Libro libro = catalogo.buscarPorIsbn(isbn);
        
        // Verificar si el libro existe
        if (libro == null) {
            System.out.println("El libro con ISBN " + isbn + " no existe en el catálogo");
            return null;
        }
        
        // Verificar si el libro ya está prestado
        if (libro.getEstado() == Estado.PRESTADO) {
            System.out.println("El libro ya está prestado");
            return null;
        }
        
        // Verificar que el número de días sea válido
        if (diasPrestamo <= 0) {
            System.out.println("El número de días debe ser positivo");
            return null;
        }
        
        // Crear el préstamo y agregarlo a la lista de activos
        Prestamo prestamo = new Prestamo(libro, LocalDate.now(), diasPrestamo);
        prestamosActivos.add(prestamo);
        
        return prestamo;
    }
    
    /**
     * Realiza la devolución de un libro
     * @param isbn El ISBN del libro a devolver
     * @return true si la devolución fue exitosa, false en caso contrario
     */
    public boolean devolverLibro(String isbn) {
        // Buscar el libro en el catálogo
        Libro libro = catalogo.buscarPorIsbn(isbn);
        
        // Verificar si el libro existe
        if (libro == null) {
            System.out.println("El libro con ISBN " + isbn + " no existe en el catálogo");
            return false;
        }
        
        // Verificar si el libro está prestado
        if (libro.getEstado() == Estado.DISPONIBLE) {
            System.out.println("El libro no está prestado");
            return false;
        }
        
        // Buscar el préstamo activo para este libro
        Prestamo prestamoActivo = buscarPrestamoActivoPorIsbn(isbn);
        
        // Verificar si se encontró el préstamo
        if (prestamoActivo == null) {
            // Esto no debería ocurrir si el libro está prestado, pero por si acaso
            System.out.println("Error: No se encontró el registro del préstamo");
            libro.cambiarEstado(Estado.DISPONIBLE); // Arreglar el estado del libro de todas formas
            return true;
        }
        
        // Finalizar el préstamo y quitarlo de la lista de activos
        prestamoActivo.finalizar();
        prestamosActivos.remove(prestamoActivo);
        
        return true;
    }
    
    /**
     * Extiende un préstamo por el número de días especificado
     * @param isbn El ISBN del libro prestado
     * @param diasExtension Número de días adicionales
     * @return true si la extensión fue exitosa, false en caso contrario
     */
    public boolean extenderPrestamo(String isbn, int diasExtension) {
        // Verificar que el número de días sea válido
        if (diasExtension <= 0) {
            System.out.println("El número de días debe ser positivo");
            return false;
        }
        
        // Buscar el préstamo activo
        Prestamo prestamo = buscarPrestamoActivoPorIsbn(isbn);
        
        // Verificar si se encontró el préstamo
        if (prestamo == null) {
            System.out.println("No hay un préstamo activo para el libro con ISBN " + isbn);
            return false;
        }
        
        // Extender el préstamo
        prestamo.extenderPrestamo(diasExtension);
        return true;
    }
    
    /**
     * Busca un préstamo activo por el ISBN del libro
     * @param isbn El ISBN del libro
     * @return El préstamo encontrado o null si no existe
     */
    private Prestamo buscarPrestamoActivoPorIsbn(String isbn) {
        for (Prestamo prestamo : prestamosActivos) {
            if (prestamo.getLibro().getIsbn().equals(isbn)) {
                return prestamo;
            }
        }
        return null;
    }
    
    /**
     * Obtiene todos los préstamos activos
     * @return Lista de préstamos activos
     */
    public List<Prestamo> obtenerPrestamosActivos() {
        return new ArrayList<>(prestamosActivos);
    }
    
    /**
     * Obtiene todos los préstamos vencidos
     * @return Lista de préstamos vencidos
     */
    public List<Prestamo> obtenerPrestamosVencidos() {
        List<Prestamo> vencidos = new ArrayList<>();
        
        for (Prestamo prestamo : prestamosActivos) {
            if (prestamo.estaVencido()) {
                vencidos.add(prestamo);
            }
        }
        
        return vencidos;
    }
    
    /**
     * Verifica si un libro está prestado actualmente
     * @param isbn El ISBN del libro
     * @return true si el libro está prestado, false en caso contrario
     */
    public boolean libroEstaPrestado(String isbn) {
        return buscarPrestamoActivoPorIsbn(isbn) != null;
    }
    
    /**
     * Obtiene el número de préstamos activos
     * @return Cantidad de préstamos activos
     */
    public int contarPrestamosActivos() {
        return prestamosActivos.size();
    }
    
    /**
     * Obtiene el número de préstamos vencidos
     * @return Cantidad de préstamos vencidos
     */
    public int contarPrestamosVencidos() {
        return obtenerPrestamosVencidos().size();
    }
} 