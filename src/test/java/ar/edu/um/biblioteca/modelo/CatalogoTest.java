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
        libro1 = new Libro("978-3-16-148410-0", "Clean Code", "Robert C. Martin");
        libro2 = new Libro("978-0-13-235088-4", "Clean Architecture", "Robert C. Martin");
        libro3 = new Libro("978-0-321-12521-5", "Design Patterns", "Erich Gamma");
        
        // Agregar los libros al catálogo
        catalogo.agregarLibro(libro1);
        catalogo.agregarLibro(libro2);
        catalogo.agregarLibro(libro3);
    }
    
    @Test
    void testAgregarLibro() {
        // Arrange
        Libro nuevoLibro = new Libro("978-1-449-37319-9", "Effective Java", "Joshua Bloch");
        
        // Act
        boolean resultado = catalogo.agregarLibro(nuevoLibro);
        
        // Assert
        assertTrue(resultado);
        assertEquals(4, catalogo.contarLibros());
        assertNotNull(catalogo.buscarPorIsbn("978-1-449-37319-9"));
    }
    
    @Test
    void testAgregarLibroNulo() {
        // Act
        boolean resultado = catalogo.agregarLibro(null);
        
        // Assert
        assertFalse(resultado);
        assertEquals(3, catalogo.contarLibros());
    }
    
    @Test
    void testAgregarLibroDuplicado() {
        // Arrange
        Libro libroDuplicado = new Libro("978-3-16-148410-0", "Otro Título", "Otro Autor");
        
        // Act
        boolean resultado = catalogo.agregarLibro(libroDuplicado);
        
        // Assert
        assertFalse(resultado);
        assertEquals(3, catalogo.contarLibros());
    }
    
    @Test
    void testBuscarPorIsbn() {
        // Act
        Libro encontrado = catalogo.buscarPorIsbn("978-3-16-148410-0");
        
        // Assert
        assertNotNull(encontrado);
        assertEquals("Clean Code", encontrado.getTitulo());
        assertEquals("Robert C. Martin", encontrado.getAutor());
    }
    
    @Test
    void testBuscarPorIsbnNoExistente() {
        // Act
        Libro encontrado = catalogo.buscarPorIsbn("isbn-no-existente");
        
        // Assert
        assertNull(encontrado);
    }
    
    @Test
    void testBuscarPorIsbnNulo() {
        // Act & Assert
        assertNull(catalogo.buscarPorIsbn(null));
        assertNull(catalogo.buscarPorIsbn(""));
        assertNull(catalogo.buscarPorIsbn("   "));
    }
    
    @Test
    void testObtenerTodosLosLibros() {
        // Act
        List<Libro> todos = catalogo.obtenerTodosLosLibros();
        
        // Assert
        assertEquals(3, todos.size());
        assertTrue(todos.contains(libro1));
        assertTrue(todos.contains(libro2));
        assertTrue(todos.contains(libro3));
    }
    
    @Test
    void testObtenerLibrosDisponibles() {
        // Arrange
        libro1.cambiarEstado(Estado.PRESTADO);
        
        // Act
        List<Libro> disponibles = catalogo.obtenerLibrosDisponibles();
        
        // Assert
        assertEquals(2, disponibles.size());
        assertFalse(disponibles.contains(libro1));
        assertTrue(disponibles.contains(libro2));
        assertTrue(disponibles.contains(libro3));
    }
    
    @Test
    void testObtenerLibrosPrestados() {
        // Arrange
        libro1.cambiarEstado(Estado.PRESTADO);
        libro3.cambiarEstado(Estado.PRESTADO);
        
        // Act
        List<Libro> prestados = catalogo.obtenerLibrosPrestados();
        
        // Assert
        assertEquals(2, prestados.size());
        assertTrue(prestados.contains(libro1));
        assertFalse(prestados.contains(libro2));
        assertTrue(prestados.contains(libro3));
    }
    
    @Test
    void testEliminarLibro() {
        // Act
        boolean resultado = catalogo.eliminarLibro("978-3-16-148410-0");
        
        // Assert
        assertTrue(resultado);
        assertEquals(2, catalogo.contarLibros());
        assertNull(catalogo.buscarPorIsbn("978-3-16-148410-0"));
    }
    
    @Test
    void testEliminarLibroNoExistente() {
        // Act
        boolean resultado = catalogo.eliminarLibro("isbn-no-existente");
        
        // Assert
        assertFalse(resultado);
        assertEquals(3, catalogo.contarLibros());
    }
    
    @Test
    void testBuscarPorAutor() {
        // Act
        List<Libro> porAutor = catalogo.buscarPorAutor("Martin");
        
        // Assert
        assertEquals(2, porAutor.size());
        assertTrue(porAutor.contains(libro1));
        assertTrue(porAutor.contains(libro2));
        assertFalse(porAutor.contains(libro3));
    }
    
    @Test
    void testBuscarPorAutorNoExistente() {
        // Act
        List<Libro> porAutor = catalogo.buscarPorAutor("Autor Inexistente");
        
        // Assert
        assertTrue(porAutor.isEmpty());
    }
    
    @Test
    void testBuscarPorTitulo() {
        // Act
        List<Libro> porTitulo = catalogo.buscarPorTitulo("Clean");
        
        // Assert
        assertEquals(2, porTitulo.size());
        assertTrue(porTitulo.contains(libro1));
        assertTrue(porTitulo.contains(libro2));
        assertFalse(porTitulo.contains(libro3));
    }
    
    @Test
    void testContarLibros() {
        // Assert
        assertEquals(3, catalogo.contarLibros());
        
        // Act
        catalogo.eliminarLibro(libro1.getIsbn());
        
        // Assert
        assertEquals(2, catalogo.contarLibros());
    }
    
    @Test
    void testContarLibrosDisponiblesYPrestados() {
        // Arrange
        assertEquals(3, catalogo.contarLibrosDisponibles());
        assertEquals(0, catalogo.contarLibrosPrestados());
        
        // Act
        libro1.cambiarEstado(Estado.PRESTADO);
        
        // Assert
        assertEquals(2, catalogo.contarLibrosDisponibles());
        assertEquals(1, catalogo.contarLibrosPrestados());
    }
} 