package ar.edu.um.biblioteca.notificacion;

/**
 * Interfaz que define el comportamiento básico para el sistema de notificaciones.
 * Permite enviar diferentes tipos de notificaciones a los usuarios.
 */
public interface Notificador {
    
    /**
     * Envía una notificación informativa.
     * 
     * @param destinatario El destinatario de la notificación
     * @param mensaje El mensaje a enviar
     * @return true si la notificación se envió correctamente, false en caso contrario
     */
    boolean enviarInformacion(String destinatario, String mensaje);
    
    /**
     * Envía una notificación de advertencia.
     * 
     * @param destinatario El destinatario de la notificación
     * @param mensaje El mensaje a enviar
     * @return true si la notificación se envió correctamente, false en caso contrario
     */
    boolean enviarAdvertencia(String destinatario, String mensaje);
    
    /**
     * Envía una notificación de error.
     * 
     * @param destinatario El destinatario de la notificación
     * @param mensaje El mensaje a enviar
     * @return true si la notificación se envió correctamente, false en caso contrario
     */
    boolean enviarError(String destinatario, String mensaje);
    
    /**
     * Envía una notificación personalizada con un título específico.
     * 
     * @param destinatario El destinatario de la notificación
     * @param titulo El título de la notificación
     * @param mensaje El mensaje a enviar
     * @return true si la notificación se envió correctamente, false en caso contrario
     */
    boolean enviarNotificacion(String destinatario, String titulo, String mensaje);
} 