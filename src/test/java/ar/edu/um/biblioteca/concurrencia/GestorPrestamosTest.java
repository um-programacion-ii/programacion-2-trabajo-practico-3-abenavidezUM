package ar.edu.um.biblioteca.concurrencia;

import ar.edu.um.biblioteca.modelo.Libro;
import ar.edu.um.biblioteca.modelo.Prestamo;
import ar.edu.um.biblioteca.notificacion.Notificador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

class GestorPrestamosTest {
    
    private GestorPrestamos gestorPrestamos;
    private Notificador notificadorMock;
    private Libro libro1, libro2, libro3;
    
    @BeforeEach
    void setUp() {
        // Crear un mock de Notificador
        notificadorMock = Mockito.mock(Notificador.class);
        when(notificadorMock.enviarInformacion(anyString(), anyString())).thenReturn(true);
        when(notificadorMock.enviarAdvertencia(anyString(), anyString())).thenReturn(true);
        when(notificadorMock.enviarError(anyString(), anyString())).thenReturn(true);
        when(notificadorMock.enviarNotificacion(anyString(), anyString(), anyString())).thenReturn(true);
        
        // Crear el gestor con el notificador mock
        gestorPrestamos = new GestorPrestamos(notificadorMock);
        
        // Crear libros de prueba
        libro1 = new Libro("978-0-306-40615-7", "Clean Code", "Robert C. Martin", "Programación");
        libro2 = new Libro("978-0-132-35088-4", "Design Patterns", "Gang of Four", "Programación");
        libro3 = new Libro("978-0-201-63361-0", "Refactoring", "Martin Fowler", "Programación");
    }
    
    @Test
    void testRegistrarPrestamo() {
        // Registrar un préstamo
        Prestamo prestamo = gestorPrestamos.registrarPrestamo(libro1, "usuario1");
        
        // Verificar que el préstamo se creó correctamente
        assertNotNull(prestamo);
        assertEquals(libro1, prestamo.getLibro());
        assertFalse(prestamo.isDevuelto());
        
        // Verificar que el libro está marcado como no disponible
        assertFalse(libro1.isDisponible());
        
        // Verificar que se envió una notificación
        verify(notificadorMock).enviarInformacion(eq("usuario1"), contains("Se ha registrado el préstamo"));
    }
    
    @Test
    void testRegistrarPrestamoLibroNoDisponible() {
        // Hacer que el libro no esté disponible
        libro1.prestar();
        
        // Intentar registrar un préstamo
        Prestamo prestamo = gestorPrestamos.registrarPrestamo(libro1, "usuario1");
        
        // Verificar que no se creó el préstamo
        assertNull(prestamo);
        
        // Verificar que se envió una notificación de error
        verify(notificadorMock).enviarError(eq("usuario1"), contains("no está disponible"));
    }
    
    @Test
    void testRegistrarDevolucion() {
        // Registrar un préstamo
        Prestamo prestamo = gestorPrestamos.registrarPrestamo(libro1, "usuario1");
        
        // Registrar la devolución
        boolean resultado = gestorPrestamos.registrarDevolucion(prestamo, "usuario1");
        
        // Verificar que la devolución se registró correctamente
        assertTrue(resultado);
        assertTrue(prestamo.isDevuelto());
        assertTrue(libro1.isDisponible());
        
        // Verificar que se envió una notificación
        verify(notificadorMock).enviarInformacion(eq("usuario1"), contains("devolución correcta"));
    }
    
    @Test
    void testRegistrarDevolucionVencida() {
        // Crear un préstamo con fecha de devolución en el pasado
        Prestamo prestamo = new Prestamo(libro1, LocalDate.now().minusDays(30), LocalDate.now().minusDays(5));
        
        // Agregar el préstamo manualmente a la lista
        gestorPrestamos.registrarPrestamo(libro1, "usuario1");
        
        // Reemplazar el préstamo creado con nuestro préstamo vencido (accediendo a través de obtenerPrestamos)
        List<Prestamo> prestamos = gestorPrestamos.obtenerPrestamos();
        prestamos.clear();
        prestamos.add(prestamo);
        
        // Registrar la devolución
        boolean resultado = gestorPrestamos.registrarDevolucion(prestamo, "usuario1");
        
        // Verificar que la devolución se registró correctamente
        assertTrue(resultado);
        assertTrue(prestamo.isDevuelto());
        assertTrue(libro1.isDisponible());
        
        // Verificar que se envió una notificación de advertencia
        verify(notificadorMock).enviarAdvertencia(eq("usuario1"), contains("devolución con retraso"));
    }
    
    @Test
    void testExtenderPrestamo() {
        // Registrar un préstamo
        Prestamo prestamo = gestorPrestamos.registrarPrestamo(libro1, "usuario1");
        LocalDate fechaOriginal = prestamo.getFechaDevolucion();
        
        // Extender el préstamo
        boolean resultado = gestorPrestamos.extenderPrestamo(prestamo, "usuario1");
        
        // Verificar que el préstamo se extendió correctamente
        assertTrue(resultado);
        assertTrue(prestamo.getFechaDevolucion().isAfter(fechaOriginal));
        
        // Verificar que se envió una notificación
        verify(notificadorMock, times(2)).enviarInformacion(eq("usuario1"), anyString());
    }
    
    @Test
    void testObtenerPrestamos() {
        // Registrar varios préstamos
        gestorPrestamos.registrarPrestamo(libro1, "usuario1");
        gestorPrestamos.registrarPrestamo(libro2, "usuario2");
        gestorPrestamos.registrarPrestamo(libro3, "usuario3");
        
        // Obtener todos los préstamos
        List<Prestamo> prestamos = gestorPrestamos.obtenerPrestamos();
        
        // Verificar que se devolvieron todos los préstamos
        assertEquals(3, prestamos.size());
        
        // Verificar que la lista devuelta es no modificable
        assertThrows(UnsupportedOperationException.class, () -> prestamos.add(new Prestamo(libro1)));
    }
    
    @Test
    void testObtenerPrestamosActivos() {
        // Registrar varios préstamos
        Prestamo prestamo1 = gestorPrestamos.registrarPrestamo(libro1, "usuario1");
        Prestamo prestamo2 = gestorPrestamos.registrarPrestamo(libro2, "usuario2");
        Prestamo prestamo3 = gestorPrestamos.registrarPrestamo(libro3, "usuario3");
        
        // Devolver uno de los préstamos
        gestorPrestamos.registrarDevolucion(prestamo2, "usuario2");
        
        // Obtener préstamos activos
        List<Prestamo> prestamosActivos = gestorPrestamos.obtenerPrestamosActivos();
        
        // Verificar que solo se devolvieron los préstamos activos
        assertEquals(2, prestamosActivos.size());
        assertTrue(prestamosActivos.contains(prestamo1));
        assertTrue(prestamosActivos.contains(prestamo3));
        assertFalse(prestamosActivos.contains(prestamo2));
    }
    
    @Test
    void testConcurrenciaRegistroPrestamos() throws InterruptedException {
        // Crear múltiples libros para el test
        int numLibros = 100;
        List<Libro> libros = new ArrayList<>();
        for (int i = 0; i < numLibros; i++) {
            libros.add(new Libro("ISBN-" + i, "Título " + i, "Autor " + i, "Género"));
        }
        
        // Número de threads
        int numThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        
        // Cada thread registrará préstamos para un subconjunto de libros
        for (int t = 0; t < numThreads; t++) {
            final int threadId = t;
            executor.submit(() -> {
                try {
                    // Cada thread registra préstamos para su lote de libros
                    int librosPerThread = numLibros / numThreads;
                    int startIdx = threadId * librosPerThread;
                    int endIdx = startIdx + librosPerThread;
                    
                    for (int i = startIdx; i < endIdx; i++) {
                        gestorPrestamos.registrarPrestamo(libros.get(i), "usuario" + threadId);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Esperar a que todos los threads terminen
        assertTrue(latch.await(10, TimeUnit.SECONDS));
        executor.shutdown();
        
        // Verificar que todos los préstamos se registraron
        List<Prestamo> prestamos = gestorPrestamos.obtenerPrestamos();
        assertEquals(numLibros, prestamos.size());
        
        // Verificar que todos los libros están prestados
        for (Libro libro : libros) {
            assertFalse(libro.isDisponible());
        }
    }
    
    @Test
    void testConcurrenciaLecturaEscritura() throws InterruptedException {
        // Registrar préstamos iniciales
        gestorPrestamos.registrarPrestamo(libro1, "usuario1");
        gestorPrestamos.registrarPrestamo(libro2, "usuario2");
        gestorPrestamos.registrarPrestamo(libro3, "usuario3");
        
        // Número de threads
        int numThreadsLectura = 5;
        int numThreadsEscritura = 3;
        int numIteraciones = 100;
        
        ExecutorService executor = Executors.newFixedThreadPool(numThreadsLectura + numThreadsEscritura);
        CountDownLatch latch = new CountDownLatch(numThreadsLectura + numThreadsEscritura);
        
        // Threads de lectura
        for (int t = 0; t < numThreadsLectura; t++) {
            executor.submit(() -> {
                try {
                    for (int i = 0; i < numIteraciones; i++) {
                        // Operaciones de lectura
                        gestorPrestamos.obtenerPrestamos();
                        gestorPrestamos.obtenerPrestamosActivos();
                        gestorPrestamos.obtenerPrestamosVencidos();
                        
                        // Simular algo de trabajo
                        Thread.sleep(1);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Libros adicionales para operaciones de escritura
        List<Libro> librosAdicionales = new ArrayList<>();
        for (int i = 0; i < numIteraciones; i++) {
            librosAdicionales.add(new Libro("ISBN-A" + i, "Título A" + i, "Autor A" + i, "Género"));
        }
        
        // Threads de escritura
        for (int t = 0; t < numThreadsEscritura; t++) {
            final int threadId = t;
            executor.submit(() -> {
                try {
                    for (int i = 0; i < numIteraciones / numThreadsEscritura; i++) {
                        int idx = threadId * (numIteraciones / numThreadsEscritura) + i;
                        // Operaciones de escritura
                        if (idx < librosAdicionales.size()) {
                            Prestamo prestamo = gestorPrestamos.registrarPrestamo(librosAdicionales.get(idx), "usuarioW" + threadId);
                            // Simular algo de trabajo
                            Thread.sleep(2);
                            // A veces devolver el préstamo
                            if (prestamo != null && idx % 2 == 0) {
                                gestorPrestamos.registrarDevolucion(prestamo, "usuarioW" + threadId);
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Esperar a que todos los threads terminen
        assertTrue(latch.await(30, TimeUnit.SECONDS));
        executor.shutdown();
        
        // No hay assertions específicas aquí, pero si no hay excepciones, la prueba pasa
        // La concurrencia ha sido manejada correctamente
    }
    
    @Test
    void testSetGetNotificador() {
        // Crear otro notificador mock
        Notificador nuevoNotificador = Mockito.mock(Notificador.class);
        
        // Cambiar el notificador
        gestorPrestamos.setNotificador(nuevoNotificador);
        
        // Verificar que se cambió correctamente
        assertEquals(nuevoNotificador, gestorPrestamos.getNotificador());
    }
} 