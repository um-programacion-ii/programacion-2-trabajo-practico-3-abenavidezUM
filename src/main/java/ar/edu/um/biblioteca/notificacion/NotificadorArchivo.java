package ar.edu.um.biblioteca.notificacion;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Implementación de la interfaz Notificador que guarda las notificaciones en un archivo.
 */
public class NotificadorArchivo implements Notificador {
    
    private final String rutaArchivo;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private boolean crearDirectorio;
    
    /**
     * Constructor que especifica la ruta del archivo donde se guardarán las notificaciones.
     * Por defecto, no crea directorios si no existen.
     * 
     * @param rutaArchivo La ruta del archivo donde se guardarán las notificaciones
     */
    public NotificadorArchivo(String rutaArchivo) {
        this(rutaArchivo, false);
    }
    
    /**
     * Constructor que especifica la ruta del archivo y si se deben crear directorios.
     * 
     * @param rutaArchivo La ruta del archivo donde se guardarán las notificaciones
     * @param crearDirectorio true para crear directorios si no existen, false en caso contrario
     */
    public NotificadorArchivo(String rutaArchivo, boolean crearDirectorio) {
        this.rutaArchivo = rutaArchivo;
        this.crearDirectorio = crearDirectorio;
        
        if (crearDirectorio) {
            crearDirectorioSiNoExiste();
        }
    }
    
    /**
     * Crea el directorio para el archivo de notificaciones si no existe.
     */
    private void crearDirectorioSiNoExiste() {
        try {
            File archivo = new File(rutaArchivo);
            File directorio = archivo.getParentFile();
            
            if (directorio != null && !directorio.exists()) {
                Files.createDirectories(Paths.get(directorio.getAbsolutePath()));
            }
        } catch (IOException e) {
            System.err.println("Error al crear el directorio para notificaciones: " + e.getMessage());
        }
    }
    
    @Override
    public boolean enviarInformacion(String destinatario, String mensaje) {
        return enviarNotificacion(destinatario, "INFORMACIÓN", mensaje);
    }
    
    @Override
    public boolean enviarAdvertencia(String destinatario, String mensaje) {
        return enviarNotificacion(destinatario, "ADVERTENCIA", mensaje);
    }
    
    @Override
    public boolean enviarError(String destinatario, String mensaje) {
        return enviarNotificacion(destinatario, "ERROR", mensaje);
    }
    
    @Override
    public boolean enviarNotificacion(String destinatario, String titulo, String mensaje) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaArchivo, true))) {
            String timestamp = LocalDateTime.now().format(formatter);
            
            writer.write("======================================\n");
            writer.write(timestamp + " | " + titulo + "\n");
            writer.write("--------------------------------------\n");
            writer.write("Para: " + destinatario + "\n");
            writer.write("Mensaje: " + mensaje + "\n");
            writer.write("======================================\n\n");
            
            return true;
        } catch (IOException e) {
            System.err.println("Error al escribir notificación en archivo: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtiene la ruta del archivo donde se guardan las notificaciones.
     * 
     * @return La ruta del archivo
     */
    public String getRutaArchivo() {
        return rutaArchivo;
    }
    
    /**
     * Verifica si la creación automática de directorios está habilitada.
     * 
     * @return true si se crean directorios automáticamente, false en caso contrario
     */
    public boolean isCrearDirectorio() {
        return crearDirectorio;
    }
    
    /**
     * Establece si se deben crear directorios automáticamente si no existen.
     * 
     * @param crearDirectorio true para crear directorios automáticamente, false en caso contrario
     */
    public void setCrearDirectorio(boolean crearDirectorio) {
        this.crearDirectorio = crearDirectorio;
        if (crearDirectorio) {
            crearDirectorioSiNoExiste();
        }
    }
} 