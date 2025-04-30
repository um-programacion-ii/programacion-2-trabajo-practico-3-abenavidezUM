package ar.edu.um.biblioteca.modelo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class PrestamoTest {
    
    private Libro libro;
    private Prestamo prestamo;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    
    @BeforeEach
    void setUp() {
        libro = new Libro("978-0-306-40615-7", "Clean Code", "Robert C. Martin", "Programación");
        fechaPrestamo = LocalDate.now();
        fechaDevolucion = fechaPrestamo.plusDays(15); // 15 días de préstamo
        prestamo = new Prestamo(libro, fechaPrestamo, fechaDevolucion);
    }
    
    @Test
    void testConstructorConLibro() {
        // Probar constructor que solo recibe libro
        Prestamo prestamoDefault = new Prestamo(libro);
        
        // Verificar que las fechas se inicializan correctamente
        assertEquals(LocalDate.now().toString(), prestamoDefault.getFechaPrestamo().toString());
        assertEquals(LocalDate.now().plusDays(Prestamo.DIAS_PRESTAMO).toString(), 
                    prestamoDefault.getFechaDevolucion().toString());
        assertEquals(libro, prestamoDefault.getLibro());
        assertFalse(prestamoDefault.isDevuelto());
    }
    
    @Test
    void testConstructorCompleto() {
        // Verificar que los atributos se inicializan correctamente
        assertEquals(libro, prestamo.getLibro());
        assertEquals(fechaPrestamo, prestamo.getFechaPrestamo());
        assertEquals(fechaDevolucion, prestamo.getFechaDevolucion());
        assertFalse(prestamo.isDevuelto());
    }
    
    @Test
    void testIsVencido() {
        // Un préstamo con fecha de devolución en el futuro no está vencido
        assertFalse(prestamo.isVencido());
        
        // Crear un préstamo con fecha de devolución en el pasado
        LocalDate fechaVencida = LocalDate.now().minusDays(5);
        Prestamo prestamoVencido = new Prestamo(libro, LocalDate.now().minusDays(20), fechaVencida);
        
        // Verificar que el préstamo está vencido
        assertTrue(prestamoVencido.isVencido());
    }
    
    @Test
    void testProrrogar() {
        // Guardar la fecha de devolución original
        LocalDate fechaDevolucionOriginal = prestamo.getFechaDevolucion();
        
        // Prorrogar el préstamo
        prestamo.prorrogar();
        
        // Verificar que la fecha de devolución se extiende correctamente
        assertEquals(fechaDevolucionOriginal.plusDays(Prestamo.DIAS_PRORROGA), prestamo.getFechaDevolucion());
        
        // Probar múltiples prórrogas
        prestamo.prorrogar();
        assertEquals(fechaDevolucionOriginal.plusDays(Prestamo.DIAS_PRORROGA * 2), prestamo.getFechaDevolucion());
    }
    
    @Test
    void testFinalizar() {
        // Verificar que el préstamo inicialmente no está devuelto
        assertFalse(prestamo.isDevuelto());
        
        // Finalizar el préstamo
        prestamo.finalizar();
        
        // Verificar que el préstamo ahora está marcado como devuelto
        assertTrue(prestamo.isDevuelto());
        
        // Verificar que el libro vuelve a estar disponible
        assertTrue(libro.isDisponible());
    }
    
    @Test
    void testGetDiasRestantes() {
        // Para un préstamo creado con fechas específicas
        long diasEsperados = ChronoUnit.DAYS.between(LocalDate.now(), fechaDevolucion);
        assertEquals(diasEsperados, prestamo.getDiasRestantes());
        
        // Para un préstamo con fecha de devolución en el pasado
        LocalDate fechaPasada = LocalDate.now().minusDays(5);
        Prestamo prestamoVencido = new Prestamo(libro, LocalDate.now().minusDays(10), fechaPasada);
        
        // Los días restantes deben ser negativos
        assertEquals(-5, prestamoVencido.getDiasRestantes());
    }
    
    @Test
    void testSettersYGetters() {
        // Probar setters
        LocalDate nuevaFechaPrestamo = LocalDate.now().minusDays(5);
        LocalDate nuevaFechaDevolucion = LocalDate.now().plusDays(10);
        Libro nuevoLibro = new Libro("978-0-132-35088-4", "Design Patterns", "Erich Gamma", "Programación");
        
        prestamo.setFechaPrestamo(nuevaFechaPrestamo);
        prestamo.setFechaDevolucion(nuevaFechaDevolucion);
        prestamo.setLibro(nuevoLibro);
        prestamo.setDevuelto(true);
        
        // Verificar getters
        assertEquals(nuevaFechaPrestamo, prestamo.getFechaPrestamo());
        assertEquals(nuevaFechaDevolucion, prestamo.getFechaDevolucion());
        assertEquals(nuevoLibro, prestamo.getLibro());
        assertTrue(prestamo.isDevuelto());
    }
    
    @Test
    void testEquals() {
        // Crear un préstamo idéntico
        Prestamo prestamoIgual = new Prestamo(libro, fechaPrestamo, fechaDevolucion);
        
        // Verificar que son iguales
        assertEquals(prestamo, prestamoIgual);
        assertEquals(prestamo.hashCode(), prestamoIgual.hashCode());
        
        // Crear un préstamo diferente
        Libro otroLibro = new Libro("978-0-132-35088-4", "Design Patterns", "Erich Gamma", "Programación");
        Prestamo prestamoDistinto = new Prestamo(otroLibro, fechaPrestamo, fechaDevolucion);
        
        // Verificar que no son iguales
        assertNotEquals(prestamo, prestamoDistinto);
        assertNotEquals(prestamo.hashCode(), prestamoDistinto.hashCode());
        
        // Comparar con null y otro tipo de objeto
        assertNotEquals(prestamo, null);
        assertNotEquals(prestamo, "No soy un préstamo");
    }
    
    @Test
    void testToString() {
        // Verificar que el método toString contiene información relevante
        String prestamoString = prestamo.toString();
        assertTrue(prestamoString.contains(libro.getIsbn()));
        assertTrue(prestamoString.contains(fechaPrestamo.toString()));
        assertTrue(prestamoString.contains(fechaDevolucion.toString()));
    }
} 