package ar.edu.um.biblioteca.modelo;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PrestamoMockTest {

    @Test
    void testPrestamoMock() {
        // Crear mock para Libro
        Libro libroMock = Mockito.mock(Libro.class);
        when(libroMock.getIsbn()).thenReturn("978-3-16-148410-0");
        when(libroMock.getTitulo()).thenReturn("Clean Code");
        when(libroMock.getAutor()).thenReturn("Robert C. Martin");
        
        // Crear mock para Prestamo
        Prestamo prestamoMock = Mockito.mock(Prestamo.class);
        LocalDate fechaPrestamo = LocalDate.now();
        LocalDate fechaDevolucion = fechaPrestamo.plusDays(14);
        
        // Configurar comportamiento del mock
        when(prestamoMock.getLibro()).thenReturn(libroMock);
        when(prestamoMock.getFechaPrestamo()).thenReturn(fechaPrestamo);
        when(prestamoMock.getFechaDevolucion()).thenReturn(fechaDevolucion);
        when(prestamoMock.estaVencido()).thenReturn(false);
        when(prestamoMock.calcularDiasRestantes()).thenReturn(14L);
        
        // Verificar comportamiento del mock
        assertEquals(libroMock, prestamoMock.getLibro());
        assertEquals(fechaPrestamo, prestamoMock.getFechaPrestamo());
        assertEquals(fechaDevolucion, prestamoMock.getFechaDevolucion());
        assertFalse(prestamoMock.estaVencido());
        assertEquals(14L, prestamoMock.calcularDiasRestantes());
        
        // Verificar interacciones con el mock
        prestamoMock.finalizar();
        verify(prestamoMock, times(1)).finalizar();
        
        prestamoMock.extenderPrestamo(7);
        verify(prestamoMock, times(1)).extenderPrestamo(7);
    }
    
    @Test
    void testCreacionRealDePrestamo() {
        // Crear un libro real
        Libro libro = new Libro("978-3-16-148410-0", "Clean Code", "Robert C. Martin");
        assertEquals(Estado.DISPONIBLE, libro.getEstado());
        
        // Crear un préstamo real
        LocalDate fechaPrestamo = LocalDate.now();
        Prestamo prestamo = new Prestamo(libro, fechaPrestamo);
        
        // Verificar que el préstamo se ha configurado correctamente
        assertEquals(libro, prestamo.getLibro());
        assertEquals(fechaPrestamo, prestamo.getFechaPrestamo());
        assertEquals(fechaPrestamo.plusDays(14), prestamo.getFechaDevolucion());
        assertEquals(Estado.PRESTADO, libro.getEstado());
        
        // Verificar que el método finalizar cambia el estado del libro
        prestamo.finalizar();
        assertEquals(Estado.DISPONIBLE, libro.getEstado());
    }
} 