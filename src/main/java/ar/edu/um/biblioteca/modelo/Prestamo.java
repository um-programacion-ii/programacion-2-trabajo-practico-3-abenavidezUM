package ar.edu.um.biblioteca.modelo;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Clase que representa un préstamo de un libro
 */
public class Prestamo {
    private Libro libro;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    private static final int DIAS_PRESTAMO_DEFAULT = 14; // 2 semanas por defecto
    
    /**
     * Constructor que crea un préstamo con el libro especificado
     * @param libro El libro a prestar
     */
    public Prestamo(Libro libro) {
        this.libro = libro;
        this.fechaPrestamo = LocalDate.now();
        this.fechaDevolucion = fechaPrestamo.plusDays(DIAS_PRESTAMO_DEFAULT);
        this.libro.cambiarEstado(Estado.PRESTADO);
    }
    
    /**
     * Constructor que crea un préstamo con el libro y fecha especificada
     * @param libro El libro a prestar
     * @param fechaPrestamo La fecha de préstamo
     */
    public Prestamo(Libro libro, LocalDate fechaPrestamo) {
        this.libro = libro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaPrestamo.plusDays(DIAS_PRESTAMO_DEFAULT);
        this.libro.cambiarEstado(Estado.PRESTADO);
    }
    
    /**
     * Constructor que crea un préstamo con el libro y fechas especificadas
     * @param libro El libro a prestar
     * @param fechaPrestamo La fecha de préstamo
     * @param diasPrestamo El número de días del préstamo
     */
    public Prestamo(Libro libro, LocalDate fechaPrestamo, int diasPrestamo) {
        this.libro = libro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaPrestamo.plusDays(diasPrestamo);
        this.libro.cambiarEstado(Estado.PRESTADO);
    }
    
    /**
     * Verifica si el préstamo está vencido en la fecha actual
     * @return true si la fecha actual es posterior a la fecha de devolución
     */
    public boolean estaVencido() {
        return LocalDate.now().isAfter(fechaDevolucion);
    }
    
    /**
     * Verifica si el préstamo estará vencido en una fecha específica
     * @param fecha La fecha en la que verificar el vencimiento
     * @return true si la fecha especificada es posterior a la fecha de devolución
     */
    public boolean estaVencidoEn(LocalDate fecha) {
        return fecha.isAfter(fechaDevolucion);
    }
    
    /**
     * Extiende la fecha de devolución por un número específico de días
     * @param dias El número de días adicionales
     */
    public void extenderPrestamo(int dias) {
        if (dias > 0) {
            this.fechaDevolucion = this.fechaDevolucion.plusDays(dias);
        }
    }
    
    /**
     * Finaliza el préstamo (devuelve el libro)
     */
    public void finalizar() {
        this.libro.cambiarEstado(Estado.DISPONIBLE);
    }
    
    /**
     * Calcula el número de días restantes del préstamo
     * @return El número de días hasta la fecha de devolución (negativo si está vencido)
     */
    public long calcularDiasRestantes() {
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), fechaDevolucion);
    }
    
    // Getters y setters
    
    public Libro getLibro() {
        return libro;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Prestamo prestamo = (Prestamo) o;
        return Objects.equals(libro, prestamo.libro) && 
               Objects.equals(fechaPrestamo, prestamo.fechaPrestamo);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(libro, fechaPrestamo);
    }
    
    @Override
    public String toString() {
        return "Prestamo{" +
                "libro=" + libro.getTitulo() +
                ", fechaPrestamo=" + fechaPrestamo +
                ", fechaDevolucion=" + fechaDevolucion +
                '}';
    }
} 