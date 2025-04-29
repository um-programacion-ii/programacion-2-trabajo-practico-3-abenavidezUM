package ar.edu.um.biblioteca.modelo;

/**
 * Clase que representa un libro en el sistema de biblioteca
 */
public class Libro {
    private String isbn;
    private String titulo;
    private String autor;
    private Estado estado;

    /**
     * Constructor para crear un nuevo libro
     * @param isbn El ISBN del libro
     * @param titulo El título del libro
     * @param autor El autor del libro
     */
    public Libro(String isbn, String titulo, String autor) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.estado = Estado.DISPONIBLE; // Por defecto, el libro está disponible
    }

    // Getters y setters
    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }
    
    /**
     * Cambia el estado del libro
     * @param nuevoEstado El nuevo estado del libro
     */
    public void cambiarEstado(Estado nuevoEstado) {
        this.estado = nuevoEstado;
    }
    
    @Override
    public String toString() {
        return "Libro{" +
                "isbn='" + isbn + '\'' +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", estado=" + estado +
                '}';
    }
} 