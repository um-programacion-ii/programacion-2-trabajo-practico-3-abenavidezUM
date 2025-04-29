package ar.edu.um.biblioteca.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LibroTest {

    @Test
    void testCrearLibroValido() {
        // Arrange & Act
        Libro libro = new Libro("978-3-16-148410-0", "Clean Code", "Robert C. Martin");
        
        // Assert
        assertEquals("978-3-16-148410-0", libro.getIsbn());
        assertEquals("Clean Code", libro.getTitulo());
        assertEquals("Robert C. Martin", libro.getAutor());
        assertEquals(Estado.DISPONIBLE, libro.getEstado());
    }

    @Test
    void testCambiarEstado() {
        // Arrange
        Libro libro = new Libro("978-3-16-148410-0", "Clean Code", "Robert C. Martin");
        
        // Act
        libro.cambiarEstado(Estado.PRESTADO);
        
        // Assert
        assertEquals(Estado.PRESTADO, libro.getEstado());
        
        // Act nuevamente para comprobar el cambio de vuelta
        libro.cambiarEstado(Estado.DISPONIBLE);
        
        // Assert nuevamente
        assertEquals(Estado.DISPONIBLE, libro.getEstado());
    }
    
    @Test
    void testSetters() {
        // Arrange
        Libro libro = new Libro("978-3-16-148410-0", "Clean Code", "Robert C. Martin");
        
        // Act
        libro.setIsbn("978-0-13-235088-4");
        libro.setTitulo("Clean Architecture");
        libro.setAutor("Uncle Bob");
        
        // Assert
        assertEquals("978-0-13-235088-4", libro.getIsbn());
        assertEquals("Clean Architecture", libro.getTitulo());
        assertEquals("Uncle Bob", libro.getAutor());
    }
    
    @Test
    void testToString() {
        // Arrange
        Libro libro = new Libro("978-3-16-148410-0", "Clean Code", "Robert C. Martin");
        
        // Act
        String resultToString = libro.toString();
        
        // Assert
        assertTrue(resultToString.contains("isbn='978-3-16-148410-0'"));
        assertTrue(resultToString.contains("titulo='Clean Code'"));
        assertTrue(resultToString.contains("autor='Robert C. Martin'"));
        assertTrue(resultToString.contains("estado=DISPONIBLE"));
    }
} 