package ar.edu.um.biblioteca.notificacion;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Implementación de la interfaz Notificador que muestra las notificaciones por consola.
 */
public class NotificadorConsola implements Notificador {
    
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_BLUE = "\u001B[34m";
    
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private boolean colorEnabled;
    
    /**
     * Constructor predeterminado que habilita el uso de colores por defecto.
     */
    public NotificadorConsola() {
        this(true);
    }
    
    /**
     * Constructor que permite especificar si se utilizarán colores en la salida.
     * 
     * @param colorEnabled true para habilitar colores, false para deshabilitar
     */
    public NotificadorConsola(boolean colorEnabled) {
        this.colorEnabled = colorEnabled;
    }
    
    @Override
    public boolean enviarInformacion(String destinatario, String mensaje) {
        return enviarNotificacion(destinatario, "INFORMACIÓN", mensaje, ANSI_GREEN);
    }
    
    @Override
    public boolean enviarAdvertencia(String destinatario, String mensaje) {
        return enviarNotificacion(destinatario, "ADVERTENCIA", mensaje, ANSI_YELLOW);
    }
    
    @Override
    public boolean enviarError(String destinatario, String mensaje) {
        return enviarNotificacion(destinatario, "ERROR", mensaje, ANSI_RED);
    }
    
    @Override
    public boolean enviarNotificacion(String destinatario, String titulo, String mensaje) {
        return enviarNotificacion(destinatario, titulo, mensaje, ANSI_BLUE);
    }
    
    /**
     * Método auxiliar para enviar notificaciones con un color específico.
     * 
     * @param destinatario El destinatario de la notificación
     * @param titulo El título de la notificación
     * @param mensaje El mensaje a enviar
     * @param color El código de color ANSI a utilizar
     * @return true si la notificación se envió correctamente
     */
    private boolean enviarNotificacion(String destinatario, String titulo, String mensaje, String color) {
        try {
            String timestamp = LocalDateTime.now().format(formatter);
            String formatoTitulo = colorEnabled ? color + titulo + ANSI_RESET : titulo;
            
            System.out.println("======================================");
            System.out.println(timestamp + " | " + formatoTitulo);
            System.out.println("--------------------------------------");
            System.out.println("Para: " + destinatario);
            System.out.println("Mensaje: " + mensaje);
            System.out.println("======================================");
            
            return true;
        } catch (Exception e) {
            System.err.println("Error al enviar notificación por consola: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Habilita o deshabilita el uso de colores en la salida por consola.
     * 
     * @param colorEnabled true para habilitar colores, false para deshabilitar
     */
    public void setColorEnabled(boolean colorEnabled) {
        this.colorEnabled = colorEnabled;
    }
    
    /**
     * Verifica si los colores están habilitados.
     * 
     * @return true si los colores están habilitados, false en caso contrario
     */
    public boolean isColorEnabled() {
        return colorEnabled;
    }
} 