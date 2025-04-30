package ar.edu.um.biblioteca;

import ar.edu.um.biblioteca.modelo.Catalogo;
import ar.edu.um.biblioteca.modelo.Libro;
import ar.edu.um.biblioteca.sistema.SistemaPrestamos;
import ar.edu.um.biblioteca.ui.ConsolaUI;

/**
 * Clase principal que inicia la aplicación de biblioteca
 */
public class BibliotecaApp {

    public static void main(String[] args) {
        // Inicializar el catálogo
        Catalogo catalogo = new Catalogo();
        
        // Agregar algunos libros de ejemplo
        agregarLibrosEjemplo(catalogo);
        
        // Inicializar el sistema de préstamos
        SistemaPrestamos sistemaPrestamos = new SistemaPrestamos(catalogo);
        
        // Inicializar la interfaz de usuario
        ConsolaUI ui = new ConsolaUI(catalogo, sistemaPrestamos);
        
        // Mostrar el menú principal
        ui.mostrarMenu();
    }
    
    /**
     * Agrega algunos libros de ejemplo al catálogo
     * @param catalogo El catálogo al que agregar los libros
     */
    private static void agregarLibrosEjemplo(Catalogo catalogo) {
        catalogo.agregarLibro(new Libro("978-0-7475-3269-9", "Harry Potter y la Piedra Filosofal", "J.K. Rowling"));
        catalogo.agregarLibro(new Libro("978-0-7475-3849-3", "Harry Potter y la Cámara Secreta", "J.K. Rowling"));
        catalogo.agregarLibro(new Libro("978-0-7475-4215-5", "Harry Potter y el Prisionero de Azkaban", "J.K. Rowling"));
        catalogo.agregarLibro(new Libro("978-0-7475-4624-5", "Harry Potter y el Cáliz de Fuego", "J.K. Rowling"));
        
        catalogo.agregarLibro(new Libro("978-84-376-0494-7", "Cien años de soledad", "Gabriel García Márquez"));
        catalogo.agregarLibro(new Libro("978-84-204-3547-3", "El amor en los tiempos del cólera", "Gabriel García Márquez"));
        
        catalogo.agregarLibro(new Libro("978-84-339-7157-9", "1984", "George Orwell"));
        catalogo.agregarLibro(new Libro("978-84-339-2031-7", "Rebelión en la granja", "George Orwell"));
        
        catalogo.agregarLibro(new Libro("978-0-553-57340-0", "El Hobbit", "J.R.R. Tolkien"));
        catalogo.agregarLibro(new Libro("978-0-618-57495-2", "El Señor de los Anillos", "J.R.R. Tolkien"));
    }
} 