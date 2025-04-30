package ar.edu.um.biblioteca.concurrencia;

import ar.edu.um.biblioteca.modelo.Libro;
import ar.edu.um.biblioteca.modelo.Prestamo;
import ar.edu.um.biblioteca.notificacion.Notificador;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.*;

class MonitorPrestamosTest {
    
    private GestorPrestamos gestorPrestamos;
    private Notificador notificadorMock;
    private MonitorPrestamos monitorPrestamos;
    private Libro libro1, libro2, libro3;
    
    @BeforeEach
    void setUp() {
        // Crear un mock de Notificador
        notificadorMock = Mockito.mock(Notificador.class);
        when(notificadorMock.enviarInformacion(anyString(), anyString())).thenReturn(true);
        when(notificadorMock.enviarAdvertencia(anyString(), anyString())).thenReturn(true);
        when(notificadorMock.enviarError(anyString(), anyString())).thenReturn(true);
        
        // Crear el gestor con el notificador mock
        gestorPrestamos = new GestorPrestamos(notificadorMock);
        
        // Crear el monitor de préstamos
        monitorPrestamos = new MonitorPrestamos(gestorPrestamos, notificadorMock);
        
        // Crear libros de prueba
        libro1 = new Libro("978-0-306-40615-7", "Clean Code", "Robert C. Martin", "Programación");
        libro2 = new Libro("978-0-132-35088-4", "Design Patterns", "Gang of Four", "Programación");
        libro3 = new Libro("978-0-201-63361-0", "Refactoring", "Martin Fowler", "Programación");
    }
    
    @AfterEach
    void tearDown() {
        // Detener el monitor si está ejecutando
        if (monitorPrestamos.isEjecutando()) {
            monitorPrestamos.detener();
        }
    }
    
    @Test
    void testIniciarDetener() {
        // Verificar estado inicial
        assertFalse(monitorPrestamos.isEjecutando());
        
        // Iniciar el monitor
        monitorPrestamos.iniciar(1);
        
        // Verificar que está ejecutando
        assertTrue(monitorPrestamos.isEjecutando());
        
        // Verificar que se envió una notificación
        verify(notificadorMock).enviarInformacion(eq("Sistema"), contains("Monitoreo de préstamos iniciado"));
        
        // Detener el monitor
        monitorPrestamos.detener();
        
        // Verificar que no está ejecutando
        assertFalse(monitorPrestamos.isEjecutando());
        
        // Verificar que se envió una notificación
        verify(notificadorMock).enviarInformacion(eq("Sistema"), contains("Monitoreo de préstamos detenido"));
    }
    
    @Test
    void testSetGetDiasAnticipacion() {
        // Verificar valor predeterminado
        assertEquals(3, monitorPrestamos.getDiasAnticipacion());
        
        // Cambiar el valor
        monitorPrestamos.setDiasAnticipacion(5);
        
        // Verificar nuevo valor
        assertEquals(5, monitorPrestamos.getDiasAnticipacion());
        
        // Probar con valor inválido
        monitorPrestamos.setDiasAnticipacion(-1);
        
        // Verificar que no se cambió
        assertEquals(5, monitorPrestamos.getDiasAnticipacion());
    }
    
    @Test
    void testVerificarAhora() {
        // Verificación sin préstamos
        boolean resultado = monitorPrestamos.verificarAhora();
        
        // Verificar resultado
        assertTrue(resultado);
        
        // Verificar que se envió notificación
        verify(notificadorMock).enviarInformacion(eq("Sistema"), contains("Verificación periódica completada"));
    }
    
    @Test
    void testVerificarPrestamosPorVencer() {
        // Configurar días de anticipación
        monitorPrestamos.setDiasAnticipacion(5);
        
        // Crear un préstamo que vence pronto (dentro del rango de advertencia)
        LocalDate fechaDevolucion = LocalDate.now().plusDays(3);
        Prestamo prestamo = new Prestamo(libro1, LocalDate.now(), fechaDevolucion);
        
        // Agregar el préstamo al gestor
        gestorPrestamos.registrarPrestamo(libro1, "usuario1");
        
        // Ejecutar verificación
        monitorPrestamos.verificarAhora();
        
        // Verificar que se envió una advertencia
        verify(notificadorMock).enviarAdvertencia(eq("Sistema"), contains("PRÉSTAMO POR VENCER"));
    }
    
    @Test
    void testVerificarPrestamosVencidos() {
        // Crear un préstamo que ya venció
        LocalDate fechaDevolucion = LocalDate.now().minusDays(5);
        Prestamo prestamoVencido = new Prestamo(libro1, LocalDate.now().minusDays(20), fechaDevolucion);
        
        // Registrar el préstamo en el gestor
        Prestamo prestamo = gestorPrestamos.registrarPrestamo(libro1, "usuario1");
        
        // Modificar el préstamo para que esté vencido (este es un poco hacky pero sirve para la prueba)
        try {
            // Usar reflexión para acceder y modificar el campo privado fechaDevolucion
            java.lang.reflect.Field campo = Prestamo.class.getDeclaredField("fechaDevolucion");
            campo.setAccessible(true);
            campo.set(prestamo, fechaDevolucion);
        } catch (Exception e) {
            fail("No se pudo modificar el préstamo para la prueba: " + e.getMessage());
        }
        
        // Ejecutar verificación
        monitorPrestamos.verificarAhora();
        
        // Verificar que se envió una notificación de error
        verify(notificadorMock).enviarError(eq("Sistema"), contains("PRÉSTAMO VENCIDO"));
    }
    
    @Test
    void testVerificarMultiplesPrestamos() {
        // Crear préstamos: uno normal, uno por vencer, uno vencido
        LocalDate hoy = LocalDate.now();
        
        // Préstamo normal (no genera alertas)
        gestorPrestamos.registrarPrestamo(libro1, "usuario1");
        
        // Préstamo por vencer (dentro del rango de alerta)
        Prestamo prestamoPorVencer = gestorPrestamos.registrarPrestamo(libro2, "usuario2");
        try {
            java.lang.reflect.Field campo = Prestamo.class.getDeclaredField("fechaDevolucion");
            campo.setAccessible(true);
            campo.set(prestamoPorVencer, hoy.plusDays(2));
        } catch (Exception e) {
            fail("No se pudo modificar el préstamo para la prueba: " + e.getMessage());
        }
        
        // Préstamo vencido
        Prestamo prestamoVencido = gestorPrestamos.registrarPrestamo(libro3, "usuario3");
        try {
            java.lang.reflect.Field campo = Prestamo.class.getDeclaredField("fechaDevolucion");
            campo.setAccessible(true);
            campo.set(prestamoVencido, hoy.minusDays(5));
        } catch (Exception e) {
            fail("No se pudo modificar el préstamo para la prueba: " + e.getMessage());
        }
        
        // Ejecutar verificación
        monitorPrestamos.verificarAhora();
        
        // Verificar que se enviaron las notificaciones correctas
        verify(notificadorMock).enviarAdvertencia(eq("Sistema"), contains("PRÉSTAMO POR VENCER"));
        verify(notificadorMock).enviarError(eq("Sistema"), contains("PRÉSTAMO VENCIDO"));
        verify(notificadorMock).enviarInformacion(eq("Sistema"), contains("Verificación periódica completada"));
    }
    
    @Test
    void testIniciarDobleNoTieneEfecto() {
        // Iniciar el monitor
        monitorPrestamos.iniciar(1);
        assertTrue(monitorPrestamos.isEjecutando());
        
        // Intentar iniciarlo nuevamente
        monitorPrestamos.iniciar(2);
        
        // Verificar que solo se envió una notificación de inicio
        verify(notificadorMock, times(1)).enviarInformacion(eq("Sistema"), contains("Monitoreo de préstamos iniciado"));
    }
    
    @Test
    void testDetenerCuandoNoEstaEjecutando() {
        // Intentar detener el monitor cuando no está ejecutando
        monitorPrestamos.detener();
        
        // Verificar que no se envió ninguna notificación de detención
        verify(notificadorMock, never()).enviarInformacion(eq("Sistema"), contains("Monitoreo de préstamos detenido"));
    }
} 