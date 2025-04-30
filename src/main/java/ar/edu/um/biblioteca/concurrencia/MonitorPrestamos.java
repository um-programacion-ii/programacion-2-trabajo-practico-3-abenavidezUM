package ar.edu.um.biblioteca.concurrencia;

import ar.edu.um.biblioteca.modelo.Prestamo;
import ar.edu.um.biblioteca.notificacion.Notificador;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Clase que monitorea periódicamente los préstamos para detectar vencimientos.
 * Utiliza un thread separado para no bloquear la aplicación principal.
 */
public class MonitorPrestamos {
    
    private final GestorPrestamos gestorPrestamos;
    private final Notificador notificador;
    private final ScheduledExecutorService scheduler;
    private final AtomicBoolean ejecutando;
    private int diasAnticipacion;
    
    /**
     * Constructor del monitor de préstamos.
     * 
     * @param gestorPrestamos El gestor de préstamos a monitorear
     * @param notificador El notificador para enviar alertas
     */
    public MonitorPrestamos(GestorPrestamos gestorPrestamos, Notificador notificador) {
        this.gestorPrestamos = gestorPrestamos;
        this.notificador = notificador;
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.ejecutando = new AtomicBoolean(false);
        this.diasAnticipacion = 3; // Por defecto, alertar con 3 días de anticipación
    }
    
    /**
     * Inicia el monitoreo periódico de préstamos.
     * 
     * @param intervaloHoras El intervalo en horas entre cada verificación
     */
    public void iniciar(int intervaloHoras) {
        if (ejecutando.compareAndSet(false, true)) {
            scheduler.scheduleAtFixedRate(
                    this::verificarPrestamos,
                    0,
                    intervaloHoras,
                    TimeUnit.HOURS
            );
            
            notificador.enviarInformacion("Sistema", 
                    "Monitoreo de préstamos iniciado. Intervalo: " + intervaloHoras + " horas.");
        }
    }
    
    /**
     * Detiene el monitoreo de préstamos.
     */
    public void detener() {
        if (ejecutando.compareAndSet(true, false)) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(30, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
                notificador.enviarInformacion("Sistema", "Monitoreo de préstamos detenido correctamente.");
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
                notificador.enviarError("Sistema", 
                        "Error al detener el monitoreo de préstamos: " + e.getMessage());
            }
        }
    }
    
    /**
     * Método que verifica los préstamos en busca de vencimientos próximos o ya ocurridos.
     * Este método se ejecuta periódicamente en un thread separado.
     */
    private void verificarPrestamos() {
        try {
            LocalDate hoy = LocalDate.now();
            
            // Obtener préstamos activos
            List<Prestamo> prestamosActivos = gestorPrestamos.obtenerPrestamosActivos();
            
            // Verificar préstamos por vencer
            for (Prestamo prestamo : prestamosActivos) {
                if (!prestamo.isDevuelto()) {
                    long diasRestantes = prestamo.getDiasRestantes();
                    
                    // Alertar de préstamos vencidos
                    if (diasRestantes < 0) {
                        notificador.enviarError("Sistema", 
                                "PRÉSTAMO VENCIDO: El libro '" + prestamo.getLibro().getTitulo() + 
                                "' debió ser devuelto hace " + Math.abs(diasRestantes) + " días.");
                    } 
                    // Alertar de préstamos por vencer pronto
                    else if (diasRestantes <= diasAnticipacion) {
                        notificador.enviarAdvertencia("Sistema", 
                                "PRÉSTAMO POR VENCER: El libro '" + prestamo.getLibro().getTitulo() + 
                                "' debe ser devuelto en " + diasRestantes + " días.");
                    }
                }
            }
            
            // Registrar actividad en el log
            notificador.enviarInformacion("Sistema", 
                    "Verificación periódica completada. Préstamos activos: " + prestamosActivos.size());
            
        } catch (Exception e) {
            notificador.enviarError("Sistema", 
                    "Error durante la verificación de préstamos: " + e.getMessage());
        }
    }
    
    /**
     * Establece el número de días de anticipación para alertar sobre préstamos por vencer.
     * 
     * @param diasAnticipacion Días de anticipación para la alerta
     */
    public void setDiasAnticipacion(int diasAnticipacion) {
        if (diasAnticipacion >= 0) {
            this.diasAnticipacion = diasAnticipacion;
        }
    }
    
    /**
     * Obtiene el número de días de anticipación para alertas.
     * 
     * @return Días de anticipación
     */
    public int getDiasAnticipacion() {
        return diasAnticipacion;
    }
    
    /**
     * Verifica si el monitor está ejecutándose.
     * 
     * @return true si el monitor está ejecutándose, false en caso contrario
     */
    public boolean isEjecutando() {
        return ejecutando.get();
    }
    
    /**
     * Ejecuta una verificación inmediata de los préstamos.
     * 
     * @return true si la verificación se realizó correctamente, false si ya hay una verificación en progreso
     */
    public boolean verificarAhora() {
        try {
            verificarPrestamos();
            return true;
        } catch (Exception e) {
            notificador.enviarError("Sistema", 
                    "Error al realizar verificación manual: " + e.getMessage());
            return false;
        }
    }
} 