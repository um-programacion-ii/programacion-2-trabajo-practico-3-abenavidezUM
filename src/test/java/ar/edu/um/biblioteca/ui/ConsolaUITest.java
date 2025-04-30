package ar.edu.um.biblioteca.ui;

import ar.edu.um.biblioteca.modelo.Catalogo;
import ar.edu.um.biblioteca.modelo.Estado;
import ar.edu.um.biblioteca.modelo.Libro;
import ar.edu.um.biblioteca.modelo.Prestamo;
import ar.edu.um.biblioteca.sistema.SistemaPrestamos;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConsolaUITest {
    
    private Catalogo catalogoMock;
    private SistemaPrestamos sistemaPrestamos;
    private Libro libroMock;
    private Prestamo prestamoMock;
    
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    private ByteArrayOutputStream outputStream;
    
    @BeforeEach
    void setUp() {
        // Configurar mock para capturar la salida
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
        
        // Crear mocks para el catálogo, libro y préstamo
        catalogoMock = Mockito.mock(Catalogo.class);
        sistemaPrestamos = Mockito.mock(SistemaPrestamos.class);
        libroMock = Mockito.mock(Libro.class);
        prestamoMock = Mockito.mock(Prestamo.class);
        
        // Configurar comportamiento básico de los mocks
        when(libroMock.getIsbn()).thenReturn("978-3-16-148410-0");
        when(libroMock.getTitulo()).thenReturn("Clean Code");
        when(libroMock.getAutor()).thenReturn("Robert C. Martin");
        when(libroMock.getEstado()).thenReturn(Estado.DISPONIBLE);
        
        when(prestamoMock.getLibro()).thenReturn(libroMock);
        when(prestamoMock.getFechaPrestamo()).thenReturn(LocalDate.now());
        when(prestamoMock.getFechaDevolucion()).thenReturn(LocalDate.now().plusDays(14));
        when(prestamoMock.calcularDiasRestantes()).thenReturn(14L);
    }
    
    @AfterEach
    void tearDown() {
        // Restaurar System.out y System.in
        System.setOut(originalOut);
        System.setIn(originalIn);
    }
    
    @Test
    void testBuscarPorIsbnExitoso() {
        // Configurar la entrada simulada
        String input = "1\n4\n"; // Seleccionar opción 1 (Buscar por ISBN) y luego opción 4 (Salir)
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Configurar comportamiento del mock
        when(catalogoMock.buscarPorIsbn("978-3-16-148410-0")).thenReturn(libroMock);
        
        // Simular ejecución del método buscarPorIsbn
        ConsolaUI ui = new ConsolaUI(catalogoMock, sistemaPrestamos);
        ui.menuBusquedaLibros();
        
        // Verificar que se llamó al método buscarPorIsbn con el ISBN correcto
        verify(catalogoMock).buscarPorIsbn("978-3-16-148410-0");
        
        // Verificar que se imprimieron los detalles del libro
        String output = outputStream.toString();
        assertTrue(output.contains("ISBN: 978-3-16-148410-0"));
        assertTrue(output.contains("Título: Clean Code"));
        assertTrue(output.contains("Autor: Robert C. Martin"));
    }
    
    @Test
    void testBuscarPorIsbnNoEncontrado() {
        // Configurar la entrada simulada
        String input = "1\n4\n"; // Seleccionar opción 1 (Buscar por ISBN) y luego opción 4 (Salir)
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Configurar comportamiento del mock
        when(catalogoMock.buscarPorIsbn("isbn-no-existente")).thenReturn(null);
        
        // Simular ejecución del método buscarPorIsbn
        ConsolaUI ui = new ConsolaUI(catalogoMock, sistemaPrestamos);
        ui.menuBusquedaLibros();
        
        // Verificar que se llamó al método buscarPorIsbn con el ISBN correcto
        verify(catalogoMock).buscarPorIsbn("isbn-no-existente");
        
        // Verificar que se mostró el mensaje de error
        String output = outputStream.toString();
        assertTrue(output.contains("No se encontró ningún libro con el ISBN: isbn-no-existente"));
    }
    
    @Test
    void testAgregarLibroExitoso() {
        // Configurar la entrada simulada
        String input = "978-3-16-148410-0\nClean Code\nRobert C. Martin\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Configurar comportamiento del mock
        when(catalogoMock.buscarPorIsbn("978-3-16-148410-0")).thenReturn(null); // El libro no existe aún
        when(catalogoMock.agregarLibro(any(Libro.class))).thenReturn(true);
        
        // Ejecutar el método a probar
        ConsolaUI ui = new ConsolaUI(catalogoMock, sistemaPrestamos);
        ui.agregarLibro();
        
        // Verificar las interacciones
        verify(catalogoMock).buscarPorIsbn("978-3-16-148410-0");
        verify(catalogoMock).agregarLibro(any(Libro.class));
        
        // Verificar el output
        String output = outputStream.toString();
        assertTrue(output.contains("Libro agregado correctamente"));
    }
    
    @Test
    void testAgregarLibroExistente() {
        // Configurar la entrada simulada
        String input = "978-3-16-148410-0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Configurar comportamiento del mock
        when(catalogoMock.buscarPorIsbn("978-3-16-148410-0")).thenReturn(libroMock); // El libro ya existe
        
        // Ejecutar el método a probar
        ConsolaUI ui = new ConsolaUI(catalogoMock, sistemaPrestamos);
        ui.agregarLibro();
        
        // Verificar las interacciones
        verify(catalogoMock).buscarPorIsbn("978-3-16-148410-0");
        verify(catalogoMock, never()).agregarLibro(any(Libro.class));
        
        // Verificar el output
        String output = outputStream.toString();
        assertTrue(output.contains("Ya existe un libro con ese ISBN"));
    }
    
    @Test
    void testRealizarPrestamoExitoso() {
        // Configurar la entrada simulada
        String input = "978-3-16-148410-0\n14\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Configurar comportamiento del mock
        when(catalogoMock.buscarPorIsbn("978-3-16-148410-0")).thenReturn(libroMock);
        when(libroMock.getEstado()).thenReturn(Estado.DISPONIBLE);
        when(sistemaPrestamos.prestarLibro("978-3-16-148410-0", 14)).thenReturn(prestamoMock);
        
        // Ejecutar el método a probar
        ConsolaUI ui = new ConsolaUI(catalogoMock, sistemaPrestamos);
        ui.realizarPrestamo();
        
        // Verificar las interacciones
        verify(catalogoMock).buscarPorIsbn("978-3-16-148410-0");
        verify(sistemaPrestamos).prestarLibro("978-3-16-148410-0", 14);
        
        // Verificar el output
        String output = outputStream.toString();
        assertTrue(output.contains("Préstamo realizado correctamente"));
    }
    
    @Test
    void testRealizarPrestamoLibroNoEncontrado() {
        // Configurar la entrada simulada
        String input = "isbn-no-existente\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Configurar comportamiento del mock
        when(catalogoMock.buscarPorIsbn("isbn-no-existente")).thenReturn(null);
        
        // Ejecutar el método a probar
        ConsolaUI ui = new ConsolaUI(catalogoMock, sistemaPrestamos);
        ui.realizarPrestamo();
        
        // Verificar las interacciones
        verify(catalogoMock).buscarPorIsbn("isbn-no-existente");
        verify(sistemaPrestamos, never()).prestarLibro(anyString(), anyInt());
        
        // Verificar el output
        String output = outputStream.toString();
        assertTrue(output.contains("No se encontró ningún libro con el ISBN: isbn-no-existente"));
    }
    
    @Test
    void testRealizarPrestamoLibroPrestado() {
        // Configurar la entrada simulada
        String input = "978-3-16-148410-0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Configurar comportamiento del mock
        when(catalogoMock.buscarPorIsbn("978-3-16-148410-0")).thenReturn(libroMock);
        when(libroMock.getEstado()).thenReturn(Estado.PRESTADO);
        
        // Ejecutar el método a probar
        ConsolaUI ui = new ConsolaUI(catalogoMock, sistemaPrestamos);
        ui.realizarPrestamo();
        
        // Verificar las interacciones
        verify(catalogoMock).buscarPorIsbn("978-3-16-148410-0");
        verify(sistemaPrestamos, never()).prestarLibro(anyString(), anyInt());
        
        // Verificar el output
        String output = outputStream.toString();
        assertTrue(output.contains("El libro ya está prestado"));
    }
    
    @Test
    void testDevolverLibroExitoso() {
        // Configurar la entrada simulada
        String input = "978-3-16-148410-0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Configurar comportamiento del mock
        when(catalogoMock.buscarPorIsbn("978-3-16-148410-0")).thenReturn(libroMock);
        when(libroMock.getEstado()).thenReturn(Estado.PRESTADO);
        when(sistemaPrestamos.devolverLibro("978-3-16-148410-0")).thenReturn(true);
        
        // Ejecutar el método a probar
        ConsolaUI ui = new ConsolaUI(catalogoMock, sistemaPrestamos);
        ui.devolverLibro();
        
        // Verificar las interacciones
        verify(catalogoMock).buscarPorIsbn("978-3-16-148410-0");
        verify(sistemaPrestamos).devolverLibro("978-3-16-148410-0");
        
        // Verificar el output
        String output = outputStream.toString();
        assertTrue(output.contains("Libro devuelto correctamente"));
    }
    
    @Test
    void testMostrarEstadisticas() {
        // Configurar comportamiento del mock
        when(catalogoMock.contarLibros()).thenReturn(10);
        when(catalogoMock.contarLibrosDisponibles()).thenReturn(7);
        when(catalogoMock.contarLibrosPrestados()).thenReturn(3);
        when(sistemaPrestamos.contarPrestamosActivos()).thenReturn(3);
        when(sistemaPrestamos.contarPrestamosVencidos()).thenReturn(1);
        
        // Ejecutar el método a probar
        ConsolaUI ui = new ConsolaUI(catalogoMock, sistemaPrestamos);
        ui.mostrarEstadisticas();
        
        // Verificar las interacciones
        verify(catalogoMock).contarLibros();
        verify(catalogoMock).contarLibrosDisponibles();
        verify(catalogoMock).contarLibrosPrestados();
        verify(sistemaPrestamos).contarPrestamosActivos();
        verify(sistemaPrestamos).contarPrestamosVencidos();
        
        // Verificar el output
        String output = outputStream.toString();
        assertTrue(output.contains("Total de libros: 10"));
        assertTrue(output.contains("Libros disponibles: 7"));
        assertTrue(output.contains("Libros prestados: 3"));
        assertTrue(output.contains("Préstamos activos: 3"));
        assertTrue(output.contains("Préstamos vencidos: 1"));
    }
    
    @Test
    void testMostrarPrestamosActivos() {
        // Configurar comportamiento del mock
        List<Prestamo> prestamosActivos = new ArrayList<>();
        prestamosActivos.add(prestamoMock);
        when(sistemaPrestamos.obtenerPrestamosActivos()).thenReturn(prestamosActivos);
        
        // Ejecutar el método a probar
        ConsolaUI ui = new ConsolaUI(catalogoMock, sistemaPrestamos);
        ui.mostrarPrestamosActivos();
        
        // Verificar las interacciones
        verify(sistemaPrestamos).obtenerPrestamosActivos();
        
        // Verificar el output
        String output = outputStream.toString();
        assertTrue(output.contains("Préstamos Activos"));
        assertTrue(output.contains("Libro: Clean Code"));
        assertTrue(output.contains("Autor: Robert C. Martin"));
    }
    
    @Test
    void testExtenderPrestamoExitoso() {
        // Configurar la entrada simulada
        String input = "978-3-16-148410-0\n7\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Configurar comportamiento del mock
        when(sistemaPrestamos.libroEstaPrestado("978-3-16-148410-0")).thenReturn(true);
        when(sistemaPrestamos.extenderPrestamo("978-3-16-148410-0", 7)).thenReturn(true);
        
        // Ejecutar el método a probar
        ConsolaUI ui = new ConsolaUI(catalogoMock, sistemaPrestamos);
        ui.extenderPrestamo();
        
        // Verificar las interacciones
        verify(sistemaPrestamos).libroEstaPrestado("978-3-16-148410-0");
        verify(sistemaPrestamos).extenderPrestamo("978-3-16-148410-0", 7);
        
        // Verificar el output
        String output = outputStream.toString();
        assertTrue(output.contains("Préstamo extendido correctamente"));
    }
} 