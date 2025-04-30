package ar.edu.um.biblioteca.notificacion;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificadorTest {
    
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    
    private NotificadorConsola notificadorConsola;
    private NotificadorArchivo notificadorArchivo;
    
    @TempDir
    Path tempDir;
    private File archivoNotificaciones;
    
    @BeforeEach
    void setUp() throws IOException {
        // Configurar captura de salida de consola
        System.setOut(new PrintStream(outContent));
        
        // Crear notificador de consola sin colores para facilitar pruebas
        notificadorConsola = new NotificadorConsola(false);
        
        // Crear archivo temporal para notificaciones
        archivoNotificaciones = tempDir.resolve("notificaciones.txt").toFile();
        notificadorArchivo = new NotificadorArchivo(archivoNotificaciones.getAbsolutePath(), true);
    }
    
    @AfterEach
    void tearDown() {
        // Restaurar System.out
        System.setOut(originalOut);
    }
    
    @Test
    void testNotificadorConsolaEnviarInformacion() {
        // Enviar notificación
        boolean resultado = notificadorConsola.enviarInformacion("Usuario1", "Mensaje de información");
        
        // Verificar resultado
        assertTrue(resultado);
        
        // Verificar contenido de la salida
        String salida = outContent.toString();
        assertTrue(salida.contains("INFORMACIÓN"));
        assertTrue(salida.contains("Usuario1"));
        assertTrue(salida.contains("Mensaje de información"));
    }
    
    @Test
    void testNotificadorConsolaEnviarAdvertencia() {
        // Enviar notificación
        boolean resultado = notificadorConsola.enviarAdvertencia("Usuario2", "Mensaje de advertencia");
        
        // Verificar resultado
        assertTrue(resultado);
        
        // Verificar contenido de la salida
        String salida = outContent.toString();
        assertTrue(salida.contains("ADVERTENCIA"));
        assertTrue(salida.contains("Usuario2"));
        assertTrue(salida.contains("Mensaje de advertencia"));
    }
    
    @Test
    void testNotificadorConsolaEnviarError() {
        // Enviar notificación
        boolean resultado = notificadorConsola.enviarError("Usuario3", "Mensaje de error");
        
        // Verificar resultado
        assertTrue(resultado);
        
        // Verificar contenido de la salida
        String salida = outContent.toString();
        assertTrue(salida.contains("ERROR"));
        assertTrue(salida.contains("Usuario3"));
        assertTrue(salida.contains("Mensaje de error"));
    }
    
    @Test
    void testNotificadorConsolaEnviarNotificacion() {
        // Enviar notificación personalizada
        boolean resultado = notificadorConsola.enviarNotificacion("Usuario4", "Título Personalizado", "Mensaje personalizado");
        
        // Verificar resultado
        assertTrue(resultado);
        
        // Verificar contenido de la salida
        String salida = outContent.toString();
        assertTrue(salida.contains("Título Personalizado"));
        assertTrue(salida.contains("Usuario4"));
        assertTrue(salida.contains("Mensaje personalizado"));
    }
    
    @Test
    void testNotificadorConsolaColorEnabled() {
        // Probar getter y setter de colorEnabled
        notificadorConsola.setColorEnabled(true);
        assertTrue(notificadorConsola.isColorEnabled());
        
        notificadorConsola.setColorEnabled(false);
        assertFalse(notificadorConsola.isColorEnabled());
    }
    
    @Test
    void testNotificadorArchivoEnviarInformacion() throws IOException {
        // Enviar notificación
        boolean resultado = notificadorArchivo.enviarInformacion("Usuario1", "Mensaje de información para archivo");
        
        // Verificar resultado
        assertTrue(resultado);
        
        // Verificar contenido del archivo
        List<String> lineas = Files.readAllLines(archivoNotificaciones.toPath());
        String contenido = String.join("\n", lineas);
        
        assertTrue(contenido.contains("INFORMACIÓN"));
        assertTrue(contenido.contains("Usuario1"));
        assertTrue(contenido.contains("Mensaje de información para archivo"));
    }
    
    @Test
    void testNotificadorArchivoEnviarAdvertencia() throws IOException {
        // Enviar notificación
        boolean resultado = notificadorArchivo.enviarAdvertencia("Usuario2", "Mensaje de advertencia para archivo");
        
        // Verificar resultado
        assertTrue(resultado);
        
        // Verificar contenido del archivo
        List<String> lineas = Files.readAllLines(archivoNotificaciones.toPath());
        String contenido = String.join("\n", lineas);
        
        assertTrue(contenido.contains("ADVERTENCIA"));
        assertTrue(contenido.contains("Usuario2"));
        assertTrue(contenido.contains("Mensaje de advertencia para archivo"));
    }
    
    @Test
    void testNotificadorArchivoEnviarError() throws IOException {
        // Enviar notificación
        boolean resultado = notificadorArchivo.enviarError("Usuario3", "Mensaje de error para archivo");
        
        // Verificar resultado
        assertTrue(resultado);
        
        // Verificar contenido del archivo
        List<String> lineas = Files.readAllLines(archivoNotificaciones.toPath());
        String contenido = String.join("\n", lineas);
        
        assertTrue(contenido.contains("ERROR"));
        assertTrue(contenido.contains("Usuario3"));
        assertTrue(contenido.contains("Mensaje de error para archivo"));
    }
    
    @Test
    void testNotificadorArchivoEnviarNotificacion() throws IOException {
        // Enviar notificación personalizada
        boolean resultado = notificadorArchivo.enviarNotificacion("Usuario4", "Título Personalizado", 
                "Mensaje personalizado para archivo");
        
        // Verificar resultado
        assertTrue(resultado);
        
        // Verificar contenido del archivo
        List<String> lineas = Files.readAllLines(archivoNotificaciones.toPath());
        String contenido = String.join("\n", lineas);
        
        assertTrue(contenido.contains("Título Personalizado"));
        assertTrue(contenido.contains("Usuario4"));
        assertTrue(contenido.contains("Mensaje personalizado para archivo"));
    }
    
    @Test
    void testNotificadorArchivoCrearDirectorio() {
        // Probar getter y setter de crearDirectorio
        notificadorArchivo.setCrearDirectorio(true);
        assertTrue(notificadorArchivo.isCrearDirectorio());
        
        notificadorArchivo.setCrearDirectorio(false);
        assertFalse(notificadorArchivo.isCrearDirectorio());
    }
    
    @Test
    void testNotificadorArchivoGetRutaArchivo() {
        // Verificar que la ruta del archivo es correcta
        assertEquals(archivoNotificaciones.getAbsolutePath(), notificadorArchivo.getRutaArchivo());
    }
    
    @Test
    void testNotificadorArchivoCrearDirectorioSiNoExiste() throws IOException {
        // Crear un directorio que no existe
        Path directorioNuevo = tempDir.resolve("directorio/que/no/existe");
        File archivoEnDirectorioNuevo = directorioNuevo.resolve("notificaciones.txt").toFile();
        
        // Crear notificador con creación de directorios habilitada
        NotificadorArchivo notificador = new NotificadorArchivo(archivoEnDirectorioNuevo.getAbsolutePath(), true);
        
        // Enviar una notificación
        boolean resultado = notificador.enviarInformacion("Usuario", "Mensaje en directorio nuevo");
        
        // Verificar que se creó el directorio y el archivo
        assertTrue(resultado);
        assertTrue(directorioNuevo.toFile().exists());
        assertTrue(archivoEnDirectorioNuevo.exists());
    }
    
    @Test
    void testMutiplesMensajesEnArchivo() throws IOException {
        // Enviar múltiples notificaciones
        notificadorArchivo.enviarInformacion("Usuario1", "Mensaje 1");
        notificadorArchivo.enviarAdvertencia("Usuario2", "Mensaje 2");
        notificadorArchivo.enviarError("Usuario3", "Mensaje 3");
        
        // Verificar contenido del archivo
        List<String> lineas = Files.readAllLines(archivoNotificaciones.toPath());
        String contenido = String.join("\n", lineas);
        
        // Verificar que contiene todos los mensajes
        assertTrue(contenido.contains("Mensaje 1"));
        assertTrue(contenido.contains("Mensaje 2"));
        assertTrue(contenido.contains("Mensaje 3"));
        assertTrue(contenido.contains("Usuario1"));
        assertTrue(contenido.contains("Usuario2"));
        assertTrue(contenido.contains("Usuario3"));
        assertTrue(contenido.contains("INFORMACIÓN"));
        assertTrue(contenido.contains("ADVERTENCIA"));
        assertTrue(contenido.contains("ERROR"));
    }
} 