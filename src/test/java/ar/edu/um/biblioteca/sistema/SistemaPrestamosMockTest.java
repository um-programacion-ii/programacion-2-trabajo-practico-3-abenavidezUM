package ar.edu.um.biblioteca.sistema;

import ar.edu.um.biblioteca.modelo.Catalogo;
import ar.edu.um.biblioteca.modelo.Estado;
import ar.edu.um.biblioteca.modelo.Libro;
import ar.edu.um.biblioteca.modelo.Prestamo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SistemaPrestamosMockTest {
    
    private SistemaPrestamos sistemaPrestamos;
    private Catalogo catalogoMock;
    private Libro libroDisponibleMock;
    private Libro libroPrestadoMock;
    
    private static final String ISBN_LIBRO_DISPONIBLE = "978-3-16-148410-0";
    private static final String ISBN_LIBRO_PRESTADO = "978-0-13-235088-4";
    private static final String ISBN_INEXISTENTE = "isbn-inexistente";
    
    @BeforeEach
    void setUp() {
        // Crear mocks para el catálogo y los libros
        catalogoMock = Mockito.mock(Catalogo.class);
        libroDisponibleMock = Mockito.mock(Libro.class);
        libroPrestadoMock = Mockito.mock(Libro.class);
        
        // Configurar comportamiento de los mocks
        when(libroDisponibleMock.getIsbn()).thenReturn(ISBN_LIBRO_DISPONIBLE);
        when(libroDisponibleMock.getTitulo()).thenReturn("Clean Code");
        when(libroDisponibleMock.getAutor()).thenReturn("Robert C. Martin");
        when(libroDisponibleMock.getEstado()).thenReturn(Estado.DISPONIBLE);
        
        when(libroPrestadoMock.getIsbn()).thenReturn(ISBN_LIBRO_PRESTADO);
        when(libroPrestadoMock.getTitulo()).thenReturn("Clean Architecture");
        when(libroPrestadoMock.getAutor()).thenReturn("Robert C. Martin");
        when(libroPrestadoMock.getEstado()).thenReturn(Estado.PRESTADO);
        
        // Configurar el comportamiento del catálogo mock
        when(catalogoMock.buscarPorIsbn(ISBN_LIBRO_DISPONIBLE)).thenReturn(libroDisponibleMock);
        when(catalogoMock.buscarPorIsbn(ISBN_LIBRO_PRESTADO)).thenReturn(libroPrestadoMock);
        when(catalogoMock.buscarPorIsbn(ISBN_INEXISTENTE)).thenReturn(null);
        
        // Crear el sistema de préstamos con el catálogo mock
        sistemaPrestamos = new SistemaPrestamos(catalogoMock);
    }
    
    @Test
    void testPrestarLibroDisponible() {
        // Act
        Prestamo prestamo = sistemaPrestamos.prestarLibro(ISBN_LIBRO_DISPONIBLE);
        
        // Assert
        assertNotNull(prestamo);
        assertEquals(libroDisponibleMock, prestamo.getLibro());
        verify(libroDisponibleMock).cambiarEstado(Estado.PRESTADO);
        assertEquals(1, sistemaPrestamos.contarPrestamosActivos());
    }
    
    @Test
    void testPrestarLibroPrestado() {
        // Act
        Prestamo prestamo = sistemaPrestamos.prestarLibro(ISBN_LIBRO_PRESTADO);
        
        // Assert
        assertNull(prestamo);
        verify(libroPrestadoMock, never()).cambiarEstado(any(Estado.class));
        assertEquals(0, sistemaPrestamos.contarPrestamosActivos());
    }
    
    @Test
    void testPrestarLibroInexistente() {
        // Act
        Prestamo prestamo = sistemaPrestamos.prestarLibro(ISBN_INEXISTENTE);
        
        // Assert
        assertNull(prestamo);
        assertEquals(0, sistemaPrestamos.contarPrestamosActivos());
    }
    
    @Test
    void testPrestarLibroConDuracionEspecifica() {
        // Act
        Prestamo prestamo = sistemaPrestamos.prestarLibro(ISBN_LIBRO_DISPONIBLE, 30);
        
        // Assert
        assertNotNull(prestamo);
        assertEquals(libroDisponibleMock, prestamo.getLibro());
        verify(libroDisponibleMock).cambiarEstado(Estado.PRESTADO);
        assertEquals(1, sistemaPrestamos.contarPrestamosActivos());
        
        // Verificar que la fecha de devolución es 30 días después de la fecha de préstamo
        LocalDate fechaPrestamo = prestamo.getFechaPrestamo();
        LocalDate fechaDevolucion = prestamo.getFechaDevolucion();
        long diasPrestamo = java.time.temporal.ChronoUnit.DAYS.between(fechaPrestamo, fechaDevolucion);
        assertEquals(30, diasPrestamo);
    }
    
    @Test
    void testPrestarLibroConDuracionInvalida() {
        // Act
        Prestamo prestamo = sistemaPrestamos.prestarLibro(ISBN_LIBRO_DISPONIBLE, -5);
        
        // Assert
        assertNull(prestamo);
        verify(libroDisponibleMock, never()).cambiarEstado(any(Estado.class));
        assertEquals(0, sistemaPrestamos.contarPrestamosActivos());
    }
    
    @Test
    void testDevolverLibroPrestado() {
        // Arrange
        // Primero prestamos un libro para tenerlo en los préstamos activos
        Prestamo prestamo = sistemaPrestamos.prestarLibro(ISBN_LIBRO_DISPONIBLE);
        assertNotNull(prestamo);
        
        // Reseteamos el estado del mock para poder verificar las llamadas siguientes
        reset(libroDisponibleMock);
        when(libroDisponibleMock.getIsbn()).thenReturn(ISBN_LIBRO_DISPONIBLE);
        when(libroDisponibleMock.getEstado()).thenReturn(Estado.PRESTADO);
        
        // Act
        boolean resultado = sistemaPrestamos.devolverLibro(ISBN_LIBRO_DISPONIBLE);
        
        // Assert
        assertTrue(resultado);
        verify(libroDisponibleMock).cambiarEstado(Estado.DISPONIBLE);
        assertEquals(0, sistemaPrestamos.contarPrestamosActivos());
    }
    
    @Test
    void testDevolverLibroNoRegistrado() {
        // Act
        boolean resultado = sistemaPrestamos.devolverLibro(ISBN_LIBRO_PRESTADO);
        
        // Assert
        assertFalse(resultado);
        verify(libroPrestadoMock, never()).cambiarEstado(any(Estado.class));
    }
    
    @Test
    void testDevolverLibroInexistente() {
        // Act
        boolean resultado = sistemaPrestamos.devolverLibro(ISBN_INEXISTENTE);
        
        // Assert
        assertFalse(resultado);
    }
    
    @Test
    void testExtenderPrestamo() {
        // Arrange
        // Primero prestamos un libro para tenerlo en los préstamos activos
        Prestamo prestamo = sistemaPrestamos.prestarLibro(ISBN_LIBRO_DISPONIBLE);
        assertNotNull(prestamo);
        LocalDate fechaDevolucionOriginal = prestamo.getFechaDevolucion();
        
        // Act
        boolean resultado = sistemaPrestamos.extenderPrestamo(ISBN_LIBRO_DISPONIBLE, 7);
        
        // Assert
        assertTrue(resultado);
        LocalDate nuevaFechaDevolucion = prestamo.getFechaDevolucion();
        long diasExtendidos = java.time.temporal.ChronoUnit.DAYS.between(fechaDevolucionOriginal, nuevaFechaDevolucion);
        assertEquals(7, diasExtendidos);
    }
    
    @Test
    void testExtenderPrestamoConDiasNegativos() {
        // Arrange
        // Primero prestamos un libro para tenerlo en los préstamos activos
        Prestamo prestamo = sistemaPrestamos.prestarLibro(ISBN_LIBRO_DISPONIBLE);
        assertNotNull(prestamo);
        LocalDate fechaDevolucionOriginal = prestamo.getFechaDevolucion();
        
        // Act
        boolean resultado = sistemaPrestamos.extenderPrestamo(ISBN_LIBRO_DISPONIBLE, -5);
        
        // Assert
        assertFalse(resultado);
        assertEquals(fechaDevolucionOriginal, prestamo.getFechaDevolucion());
    }
    
    @Test
    void testExtenderPrestamoLibroNoRegistrado() {
        // Act
        boolean resultado = sistemaPrestamos.extenderPrestamo(ISBN_LIBRO_PRESTADO, 7);
        
        // Assert
        assertFalse(resultado);
    }
    
    @Test
    void testObtenerPrestamosVencidos() {
        // Arrange
        // No hay manera directa de simular préstamos vencidos con los mocks que tenemos
        // porque el método estaVencido() de Prestamo depende de la fecha actual
        // En un escenario real, necesitaríamos más configuración para esto
        
        // En lugar de eso, verificamos que inicialmente no hay préstamos vencidos
        List<Prestamo> vencidos = sistemaPrestamos.obtenerPrestamosVencidos();
        assertEquals(0, vencidos.size());
    }
    
    @Test
    void testContarPrestamosVencidosConMocks() {
        // Arrange: Crear mocks para préstamos
        Prestamo prestamoVencidoMock = Mockito.mock(Prestamo.class);
        Prestamo prestamoNoVencidoMock = Mockito.mock(Prestamo.class);
        
        // Configurar comportamiento
        when(prestamoVencidoMock.estaVencido()).thenReturn(true);
        when(prestamoVencidoMock.getLibro()).thenReturn(libroDisponibleMock);
        
        when(prestamoNoVencidoMock.estaVencido()).thenReturn(false);
        when(prestamoNoVencidoMock.getLibro()).thenReturn(libroPrestadoMock);
        
        // Crear una nueva instancia de SistemaPrestamos con una lista personalizada
        SistemaPrestamos sistemaConPrestamos = new SistemaPrestamos(catalogoMock);
        
        // Acceder a la lista de préstamos activos mediante reflexión
        try {
            java.lang.reflect.Field field = SistemaPrestamos.class.getDeclaredField("prestamosActivos");
            field.setAccessible(true);
            List<Prestamo> prestamos = new ArrayList<>();
            prestamos.add(prestamoVencidoMock);
            prestamos.add(prestamoNoVencidoMock);
            field.set(sistemaConPrestamos, prestamos);
        } catch (Exception e) {
            fail("No se pudo acceder al campo prestamosActivos: " + e.getMessage());
        }
        
        // Act
        List<Prestamo> prestamosVencidos = sistemaConPrestamos.obtenerPrestamosVencidos();
        int cantidadVencidos = sistemaConPrestamos.contarPrestamosVencidos();
        
        // Assert
        assertEquals(1, prestamosVencidos.size());
        assertEquals(prestamoVencidoMock, prestamosVencidos.get(0));
        assertEquals(1, cantidadVencidos);
        verify(prestamoVencidoMock).estaVencido();
        verify(prestamoNoVencidoMock).estaVencido();
    }
    
    @Test
    void testLibroEstaPrestado() {
        // Arrange
        // Primero prestamos un libro para tenerlo en los préstamos activos
        Prestamo prestamo = sistemaPrestamos.prestarLibro(ISBN_LIBRO_DISPONIBLE);
        assertNotNull(prestamo);
        
        // Act & Assert
        assertTrue(sistemaPrestamos.libroEstaPrestado(ISBN_LIBRO_DISPONIBLE));
        assertFalse(sistemaPrestamos.libroEstaPrestado(ISBN_LIBRO_PRESTADO));
        assertFalse(sistemaPrestamos.libroEstaPrestado(ISBN_INEXISTENTE));
    }
} 