package ar.edu.um.biblioteca.concurrencia;

import ar.edu.um.biblioteca.modelo.Libro;
import ar.edu.um.biblioteca.modelo.Prestamo;
import ar.edu.um.biblioteca.notificacion.Notificador;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Clase que gestiona los préstamos de libros con soporte para concurrencia.
 * Utiliza ReadWriteLock para permitir múltiples lecturas simultáneas pero escrituras exclusivas.
 */
public class GestorPrestamos {
    
    private List<Prestamo> prestamos;
    private final ReadWriteLock lock;
    private Notificador notificador;
    
    /**
     * Constructor que inicializa el gestor con una lista vacía de préstamos y el notificador especificado.
     * 
     * @param notificador El notificador a utilizar para enviar notificaciones
     */
    public GestorPrestamos(Notificador notificador) {
        this.prestamos = new ArrayList<>();
        this.lock = new ReentrantReadWriteLock();
        this.notificador = notificador;
    }
    
    /**
     * Registra un nuevo préstamo en el sistema de forma sincronizada.
     * 
     * @param libro El libro que será prestado
     * @param usuario El nombre del usuario que solicita el préstamo
     * @return El préstamo creado o null si el libro no está disponible
     */
    public Prestamo registrarPrestamo(Libro libro, String usuario) {
        // Adquiere el lock de escritura para modificar la lista de préstamos
        lock.writeLock().lock();
        try {
            // Verificar si el libro está disponible
            if (libro == null || !libro.isDisponible()) {
                if (libro != null) {
                    notificador.enviarError(usuario, 
                            "No se puede prestar el libro '" + libro.getTitulo() + "' porque no está disponible.");
                }
                return null;
            }
            
            // Crear el préstamo
            Prestamo prestamo = new Prestamo(libro);
            prestamos.add(prestamo);
            
            // Enviar notificación de préstamo exitoso
            notificador.enviarInformacion(usuario, 
                    "Se ha registrado el préstamo del libro '" + libro.getTitulo() + 
                    "'. Fecha de devolución: " + prestamo.getFechaDevolucion());
            
            return prestamo;
        } finally {
            // Liberar el lock de escritura
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Registra la devolución de un préstamo de forma sincronizada.
     * 
     * @param prestamo El préstamo a finalizar
     * @param usuario El nombre del usuario que devuelve el libro
     * @return true si se pudo registrar la devolución, false en caso contrario
     */
    public boolean registrarDevolucion(Prestamo prestamo, String usuario) {
        // Adquiere el lock de escritura
        lock.writeLock().lock();
        try {
            if (prestamo == null || prestamo.isDevuelto() || !prestamos.contains(prestamo)) {
                if (prestamo != null) {
                    notificador.enviarError(usuario, 
                            "No se puede registrar la devolución porque el préstamo no existe o ya fue devuelto.");
                }
                return false;
            }
            
            // Registrar la devolución
            prestamo.finalizar();
            
            // Determinar si hay retraso en la devolución
            if (prestamo.isVencido()) {
                notificador.enviarAdvertencia(usuario, 
                        "Se ha registrado la devolución con retraso del libro '" + 
                        prestamo.getLibro().getTitulo() + "'. Pueden aplicarse penalizaciones.");
            } else {
                notificador.enviarInformacion(usuario, 
                        "Se ha registrado la devolución correcta del libro '" + 
                        prestamo.getLibro().getTitulo() + "'.");
            }
            
            return true;
        } finally {
            // Liberar el lock de escritura
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Extiende la fecha de devolución de un préstamo de forma sincronizada.
     * 
     * @param prestamo El préstamo a extender
     * @param usuario El nombre del usuario que solicita la extensión
     * @return true si se pudo extender el préstamo, false en caso contrario
     */
    public boolean extenderPrestamo(Prestamo prestamo, String usuario) {
        // Adquiere el lock de escritura
        lock.writeLock().lock();
        try {
            if (prestamo == null || prestamo.isDevuelto() || !prestamos.contains(prestamo)) {
                if (prestamo != null) {
                    notificador.enviarError(usuario, 
                            "No se puede extender el préstamo porque no existe o ya fue devuelto.");
                }
                return false;
            }
            
            // Guardar fecha anterior para mensaje
            LocalDate fechaAnterior = prestamo.getFechaDevolucion();
            
            // Extender el préstamo
            prestamo.prorrogar();
            
            // Notificar al usuario
            notificador.enviarInformacion(usuario, 
                    "Se ha extendido el préstamo del libro '" + prestamo.getLibro().getTitulo() + 
                    "'. Nueva fecha de devolución: " + prestamo.getFechaDevolucion() + 
                    " (anterior: " + fechaAnterior + ")");
            
            return true;
        } finally {
            // Liberar el lock de escritura
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Obtiene una lista de todos los préstamos actuales de forma sincronizada.
     * 
     * @return Una lista no modificable de todos los préstamos
     */
    public List<Prestamo> obtenerPrestamos() {
        // Adquiere el lock de lectura para leer la lista de préstamos
        lock.readLock().lock();
        try {
            return Collections.unmodifiableList(new ArrayList<>(prestamos));
        } finally {
            // Liberar el lock de lectura
            lock.readLock().unlock();
        }
    }
    
    /**
     * Obtiene una lista de los préstamos activos (no devueltos) de forma sincronizada.
     * 
     * @return Una lista de los préstamos activos
     */
    public List<Prestamo> obtenerPrestamosActivos() {
        // Adquiere el lock de lectura
        lock.readLock().lock();
        try {
            List<Prestamo> activos = new ArrayList<>();
            for (Prestamo prestamo : prestamos) {
                if (!prestamo.isDevuelto()) {
                    activos.add(prestamo);
                }
            }
            return Collections.unmodifiableList(activos);
        } finally {
            // Liberar el lock de lectura
            lock.readLock().unlock();
        }
    }
    
    /**
     * Obtiene una lista de los préstamos vencidos (no devueltos y con fecha de devolución pasada).
     * 
     * @return Una lista de los préstamos vencidos
     */
    public List<Prestamo> obtenerPrestamosVencidos() {
        // Adquiere el lock de lectura
        lock.readLock().lock();
        try {
            List<Prestamo> vencidos = new ArrayList<>();
            for (Prestamo prestamo : prestamos) {
                if (!prestamo.isDevuelto() && prestamo.isVencido()) {
                    vencidos.add(prestamo);
                }
            }
            return Collections.unmodifiableList(vencidos);
        } finally {
            // Liberar el lock de lectura
            lock.readLock().unlock();
        }
    }
    
    /**
     * Establece el notificador a utilizar.
     * 
     * @param notificador El notificador a utilizar
     */
    public void setNotificador(Notificador notificador) {
        lock.writeLock().lock();
        try {
            this.notificador = notificador;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    /**
     * Obtiene el notificador actual.
     * 
     * @return El notificador utilizado por este gestor
     */
    public Notificador getNotificador() {
        lock.readLock().lock();
        try {
            return notificador;
        } finally {
            lock.readLock().unlock();
        }
    }
} 