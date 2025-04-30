package ar.edu.um.biblioteca.modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CatalogoTest {
    
    private Catalogo catalogo;
    private Libro libro1;
    private Libro libro2;
    private Libro libro3;
    
    @BeforeEach
    void setUp() {
        catalogo = new Catalogo();
        
        // Crear algunos libros para las pruebas
        libro1 = new Libro("978-0-306-40615-7", "Clean Code", "Robert C. Martin", "Programación");
        libro2 = new Libro("978-0-132-35088-4", "Design Patterns", "Erich Gamma", "Programación");
        libro3 = new Libro("978-1-449-33737-8", "Effective Java", "Joshua Bloch", "Java");
    }
    
    @Test
    void testConstructor() {
        // Verificar que el catálogo se inicializa vacío
        assertEquals(0, catalogo.obtenerCantidadLibros());
        assertTrue(catalogo.obtenerLibros().isEmpty());
    }
    
    @Test
    void testAgregarLibro() {
        // Agregar un libro al catálogo
        catalogo.agregarLibro(libro1);
        
        // Verificar que se agregó correctamente
        assertEquals(1, catalogo.obtenerCantidadLibros());
        assertTrue(catalogo.obtenerLibros().contains(libro1));
        
        // Agregar otro libro
        catalogo.agregarLibro(libro2);
        assertEquals(2, catalogo.obtenerCantidadLibros());
        assertTrue(catalogo.obtenerLibros().contains(libro2));
        
        // Intentar agregar un libro null no debería modificar el catálogo
        catalogo.agregarLibro(null);
        assertEquals(2, catalogo.obtenerCantidadLibros());
    }
    
    @Test
    void testBuscarLibroPorISBN() {
        // Agregar libros al catálogo
        catalogo.agregarLibro(libro1);
        catalogo.agregarLibro(libro2);
        
        // Buscar un libro existente por ISBN
        Libro libroEncontrado = catalogo.buscarLibroPorISBN(libro1.getIsbn());
        assertNotNull(libroEncontrado);
        assertEquals(libro1.getIsbn(), libroEncontrado.getIsbn());
        
        // Buscar un libro que no existe
        Libro libroNoEncontrado = catalogo.buscarLibroPorISBN("ISBN-NO-EXISTENTE");
        assertNull(libroNoEncontrado);
        
        // Buscar con ISBN null o vacío
        assertNull(catalogo.buscarLibroPorISBN(null));
        assertNull(catalogo.buscarLibroPorISBN(""));
    }
    
    @Test
    void testBuscarLibrosPorAutor() {
        // Agregar libros al catálogo
        catalogo.agregarLibro(libro1);
        catalogo.agregarLibro(libro2);
        catalogo.agregarLibro(libro3);
        
        // Buscar libros por autor existente
        List<Libro> librosMartin = catalogo.buscarLibrosPorAutor("Robert C. Martin");
        assertEquals(1, librosMartin.size());
        assertTrue(librosMartin.contains(libro1));
        
        // Buscar con un autor que tiene múltiples libros
        // Agregamos otro libro del mismo autor
        Libro libro4 = new Libro("978-0-132-35089-1", "Clean Architecture", "Robert C. Martin", "Programación");
        catalogo.agregarLibro(libro4);
        
        List<Libro> librosMartin2 = catalogo.buscarLibrosPorAutor("Robert C. Martin");
        assertEquals(2, librosMartin2.size());
        assertTrue(librosMartin2.contains(libro1));
        assertTrue(librosMartin2.contains(libro4));
        
        // Buscar con un autor que no tiene libros
        List<Libro> librosNoExistente = catalogo.buscarLibrosPorAutor("Autor No Existente");
        assertTrue(librosNoExistente.isEmpty());
        
        // Buscar con autor null o vacío
        assertTrue(catalogo.buscarLibrosPorAutor(null).isEmpty());
        assertTrue(catalogo.buscarLibrosPorAutor("").isEmpty());
    }
    
    @Test
    void testBuscarLibrosPorTitulo() {
        // Agregar libros al catálogo
        catalogo.agregarLibro(libro1);
        catalogo.agregarLibro(libro2);
        catalogo.agregarLibro(libro3);
        
        // Buscar libros por título existente
        List<Libro> librosClean = catalogo.buscarLibrosPorTitulo("Clean Code");
        assertEquals(1, librosClean.size());
        assertTrue(librosClean.contains(libro1));
        
        // Buscar con un título parcial
        List<Libro> librosJava = catalogo.buscarLibrosPorTitulo("Java");
        assertEquals(1, librosJava.size());
        assertTrue(librosJava.contains(libro3));
        
        // Buscar título que no existe
        List<Libro> librosNoExistente = catalogo.buscarLibrosPorTitulo("Título No Existente");
        assertTrue(librosNoExistente.isEmpty());
        
        // Buscar con título null o vacío
        assertTrue(catalogo.buscarLibrosPorTitulo(null).isEmpty());
        assertTrue(catalogo.buscarLibrosPorTitulo("").isEmpty());
    }
    
    @Test
    void testEliminarLibro() {
        // Agregar libros al catálogo
        catalogo.agregarLibro(libro1);
        catalogo.agregarLibro(libro2);
        assertEquals(2, catalogo.obtenerCantidadLibros());
        
        // Eliminar un libro existente
        boolean eliminado = catalogo.eliminarLibro(libro1);
        assertTrue(eliminado);
        assertEquals(1, catalogo.obtenerCantidadLibros());
        assertFalse(catalogo.obtenerLibros().contains(libro1));
        assertTrue(catalogo.obtenerLibros().contains(libro2));
        
        // Intentar eliminar un libro que no está en el catálogo
        boolean noEliminado = catalogo.eliminarLibro(libro3);
        assertFalse(noEliminado);
        assertEquals(1, catalogo.obtenerCantidadLibros());
        
        // Intentar eliminar null
        assertFalse(catalogo.eliminarLibro(null));
    }
    
    @Test
    void testEliminarLibroPorISBN() {
        // Agregar libros al catálogo
        catalogo.agregarLibro(libro1);
        catalogo.agregarLibro(libro2);
        assertEquals(2, catalogo.obtenerCantidadLibros());
        
        // Eliminar un libro existente por ISBN
        boolean eliminado = catalogo.eliminarLibroPorISBN(libro1.getIsbn());
        assertTrue(eliminado);
        assertEquals(1, catalogo.obtenerCantidadLibros());
        assertNull(catalogo.buscarLibroPorISBN(libro1.getIsbn()));
        assertNotNull(catalogo.buscarLibroPorISBN(libro2.getIsbn()));
        
        // Intentar eliminar un libro con ISBN que no existe
        boolean noEliminado = catalogo.eliminarLibroPorISBN("ISBN-NO-EXISTENTE");
        assertFalse(noEliminado);
        assertEquals(1, catalogo.obtenerCantidadLibros());
        
        // Intentar eliminar con ISBN null o vacío
        assertFalse(catalogo.eliminarLibroPorISBN(null));
        assertFalse(catalogo.eliminarLibroPorISBN(""));
    }
    
    @Test
    void testObtenerLibrosDisponibles() {
        // Agregar libros al catálogo
        catalogo.agregarLibro(libro1);
        catalogo.agregarLibro(libro2);
        catalogo.agregarLibro(libro3);
        
        // Inicialmente todos los libros deberían estar disponibles
        List<Libro> disponibles = catalogo.obtenerLibrosDisponibles();
        assertEquals(3, disponibles.size());
        assertTrue(disponibles.contains(libro1));
        assertTrue(disponibles.contains(libro2));
        assertTrue(disponibles.contains(libro3));
        
        // Cambiar el estado de un libro a PRESTADO
        libro1.prestar();
        
        // Ahora solo dos libros deberían estar disponibles
        disponibles = catalogo.obtenerLibrosDisponibles();
        assertEquals(2, disponibles.size());
        assertFalse(disponibles.contains(libro1));
        assertTrue(disponibles.contains(libro2));
        assertTrue(disponibles.contains(libro3));
    }
    
    @Test
    void testObtenerLibrosPrestados() {
        // Agregar libros al catálogo
        catalogo.agregarLibro(libro1);
        catalogo.agregarLibro(libro2);
        catalogo.agregarLibro(libro3);
        
        // Inicialmente no debería haber libros prestados
        List<Libro> prestados = catalogo.obtenerLibrosPrestados();
        assertTrue(prestados.isEmpty());
        
        // Cambiar el estado de dos libros a PRESTADO
        libro1.prestar();
        libro3.prestar();
        
        // Ahora debería haber dos libros prestados
        prestados = catalogo.obtenerLibrosPrestados();
        assertEquals(2, prestados.size());
        assertTrue(prestados.contains(libro1));
        assertFalse(prestados.contains(libro2));
        assertTrue(prestados.contains(libro3));
    }
    
    @Test
    void testObtenerCantidadLibrosDisponibles() {
        // Agregar libros al catálogo
        catalogo.agregarLibro(libro1);
        catalogo.agregarLibro(libro2);
        catalogo.agregarLibro(libro3);
        
        // Inicialmente todos los libros deberían estar disponibles
        assertEquals(3, catalogo.obtenerCantidadLibrosDisponibles());
        
        // Cambiar el estado de dos libros a PRESTADO
        libro1.prestar();
        libro3.prestar();
        
        // Ahora solo un libro debería estar disponible
        assertEquals(1, catalogo.obtenerCantidadLibrosDisponibles());
    }
    
    @Test
    void testObtenerCantidadLibrosPrestados() {
        // Agregar libros al catálogo
        catalogo.agregarLibro(libro1);
        catalogo.agregarLibro(libro2);
        catalogo.agregarLibro(libro3);
        
        // Inicialmente no debería haber libros prestados
        assertEquals(0, catalogo.obtenerCantidadLibrosPrestados());
        
        // Cambiar el estado de dos libros a PRESTADO
        libro1.prestar();
        libro3.prestar();
        
        // Ahora debería haber dos libros prestados
        assertEquals(2, catalogo.obtenerCantidadLibrosPrestados());
        
        // Devolver un libro
        libro1.devolver();
        
        // Ahora debería haber un libro prestado
        assertEquals(1, catalogo.obtenerCantidadLibrosPrestados());
    }
} 