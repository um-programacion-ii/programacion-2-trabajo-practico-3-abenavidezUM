package ar.edu.um.biblioteca.ui;

import ar.edu.um.biblioteca.modelo.Catalogo;
import ar.edu.um.biblioteca.modelo.Estado;
import ar.edu.um.biblioteca.modelo.Libro;
import ar.edu.um.biblioteca.modelo.Prestamo;
import ar.edu.um.biblioteca.sistema.SistemaPrestamos;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * Interfaz de usuario basada en consola para el sistema de biblioteca
 */
public class ConsolaUI {
    private final Scanner scanner;
    private final Catalogo catalogo;
    private final SistemaPrestamos sistemaPrestamos;
    private final DateTimeFormatter dateFormatter;
    
    /**
     * Constructor para la interfaz de consola
     * @param catalogo El catálogo a utilizar
     * @param sistemaPrestamos El sistema de préstamos a utilizar
     */
    public ConsolaUI(Catalogo catalogo, SistemaPrestamos sistemaPrestamos) {
        this.scanner = new Scanner(System.in);
        this.catalogo = catalogo;
        this.sistemaPrestamos = sistemaPrestamos;
        this.dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }
    
    /**
     * Muestra el menú principal y procesa las opciones del usuario
     */
    public void mostrarMenu() {
        boolean salir = false;
        
        while (!salir) {
            System.out.println("\n===== SISTEMA DE BIBLIOTECA =====");
            System.out.println("1. Gestionar Libros");
            System.out.println("2. Gestionar Préstamos");
            System.out.println("3. Ver Estadísticas");
            System.out.println("4. Salir");
            System.out.print("Seleccione una opción: ");
            
            int opcion = obtenerEntero();
            
            switch (opcion) {
                case 1:
                    menuLibros();
                    break;
                case 2:
                    menuPrestamos();
                    break;
                case 3:
                    mostrarEstadisticas();
                    break;
                case 4:
                    salir = true;
                    System.out.println("¡Gracias por usar el sistema de biblioteca!");
                    break;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
                    break;
            }
        }
    }
    
    /**
     * Muestra el menú de gestión de libros
     */
    private void menuLibros() {
        boolean volver = false;
        
        while (!volver) {
            System.out.println("\n----- Gestión de Libros -----");
            System.out.println("1. Agregar Libro");
            System.out.println("2. Buscar Libro");
            System.out.println("3. Ver Todos los Libros");
            System.out.println("4. Eliminar Libro");
            System.out.println("5. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            
            int opcion = obtenerEntero();
            
            switch (opcion) {
                case 1:
                    agregarLibro();
                    break;
                case 2:
                    menuBusquedaLibros();
                    break;
                case 3:
                    mostrarTodosLosLibros();
                    break;
                case 4:
                    eliminarLibro();
                    break;
                case 5:
                    volver = true;
                    break;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
                    break;
            }
        }
    }
    
    /**
     * Muestra el menú de búsqueda de libros
     */
    private void menuBusquedaLibros() {
        System.out.println("\n----- Búsqueda de Libros -----");
        System.out.println("1. Buscar por ISBN");
        System.out.println("2. Buscar por Título");
        System.out.println("3. Buscar por Autor");
        System.out.println("4. Volver");
        System.out.print("Seleccione una opción: ");
        
        int opcion = obtenerEntero();
        
        switch (opcion) {
            case 1:
                buscarPorIsbn();
                break;
            case 2:
                buscarPorTitulo();
                break;
            case 3:
                buscarPorAutor();
                break;
            case 4:
                // Volver al menú anterior
                break;
            default:
                System.out.println("Opción no válida. Intente nuevamente.");
                break;
        }
    }
    
    /**
     * Muestra el menú de gestión de préstamos
     */
    private void menuPrestamos() {
        boolean volver = false;
        
        while (!volver) {
            System.out.println("\n----- Gestión de Préstamos -----");
            System.out.println("1. Realizar Préstamo");
            System.out.println("2. Devolver Libro");
            System.out.println("3. Extender Préstamo");
            System.out.println("4. Ver Préstamos Activos");
            System.out.println("5. Ver Préstamos Vencidos");
            System.out.println("6. Volver al menú principal");
            System.out.print("Seleccione una opción: ");
            
            int opcion = obtenerEntero();
            
            switch (opcion) {
                case 1:
                    realizarPrestamo();
                    break;
                case 2:
                    devolverLibro();
                    break;
                case 3:
                    extenderPrestamo();
                    break;
                case 4:
                    mostrarPrestamosActivos();
                    break;
                case 5:
                    mostrarPrestamosVencidos();
                    break;
                case 6:
                    volver = true;
                    break;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
                    break;
            }
        }
    }
    
    // Métodos para la gestión de libros
    
    /**
     * Agrega un nuevo libro al catálogo
     */
    private void agregarLibro() {
        System.out.println("\n----- Agregar Libro -----");
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine();
        
        // Verificar si el libro ya existe
        if (catalogo.buscarPorIsbn(isbn) != null) {
            System.out.println("Ya existe un libro con ese ISBN.");
            return;
        }
        
        System.out.print("Título: ");
        String titulo = scanner.nextLine();
        
        System.out.print("Autor: ");
        String autor = scanner.nextLine();
        
        Libro nuevoLibro = new Libro(isbn, titulo, autor);
        boolean resultado = catalogo.agregarLibro(nuevoLibro);
        
        if (resultado) {
            System.out.println("Libro agregado correctamente.");
        } else {
            System.out.println("No se pudo agregar el libro.");
        }
    }
    
    /**
     * Busca un libro por su ISBN
     */
    private void buscarPorIsbn() {
        System.out.println("\n----- Buscar por ISBN -----");
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine();
        
        Libro libro = catalogo.buscarPorIsbn(isbn);
        
        if (libro != null) {
            mostrarDetallesLibro(libro);
        } else {
            System.out.println("No se encontró ningún libro con el ISBN: " + isbn);
        }
    }
    
    /**
     * Busca libros por título
     */
    private void buscarPorTitulo() {
        System.out.println("\n----- Buscar por Título -----");
        System.out.print("Título: ");
        String titulo = scanner.nextLine();
        
        List<Libro> libros = catalogo.buscarPorTitulo(titulo);
        
        if (!libros.isEmpty()) {
            System.out.println("\nLibros encontrados:");
            for (Libro libro : libros) {
                mostrarDetallesLibro(libro);
                System.out.println("----------------------------");
            }
        } else {
            System.out.println("No se encontraron libros con el título: " + titulo);
        }
    }
    
    /**
     * Busca libros por autor
     */
    private void buscarPorAutor() {
        System.out.println("\n----- Buscar por Autor -----");
        System.out.print("Autor: ");
        String autor = scanner.nextLine();
        
        List<Libro> libros = catalogo.buscarPorAutor(autor);
        
        if (!libros.isEmpty()) {
            System.out.println("\nLibros encontrados:");
            for (Libro libro : libros) {
                mostrarDetallesLibro(libro);
                System.out.println("----------------------------");
            }
        } else {
            System.out.println("No se encontraron libros del autor: " + autor);
        }
    }
    
    /**
     * Muestra todos los libros del catálogo
     */
    private void mostrarTodosLosLibros() {
        List<Libro> libros = catalogo.obtenerTodosLosLibros();
        
        if (!libros.isEmpty()) {
            System.out.println("\n----- Todos los Libros -----");
            for (Libro libro : libros) {
                mostrarDetallesLibro(libro);
                System.out.println("----------------------------");
            }
        } else {
            System.out.println("El catálogo está vacío.");
        }
    }
    
    /**
     * Elimina un libro del catálogo
     */
    private void eliminarLibro() {
        System.out.println("\n----- Eliminar Libro -----");
        System.out.print("ISBN del libro a eliminar: ");
        String isbn = scanner.nextLine();
        
        Libro libro = catalogo.buscarPorIsbn(isbn);
        
        if (libro == null) {
            System.out.println("No se encontró ningún libro con el ISBN: " + isbn);
            return;
        }
        
        if (libro.getEstado() == Estado.PRESTADO) {
            System.out.println("No se puede eliminar un libro que está prestado.");
            return;
        }
        
        boolean resultado = catalogo.eliminarLibro(isbn);
        
        if (resultado) {
            System.out.println("Libro eliminado correctamente.");
        } else {
            System.out.println("No se pudo eliminar el libro.");
        }
    }
    
    /**
     * Muestra los detalles de un libro
     */
    private void mostrarDetallesLibro(Libro libro) {
        System.out.println("ISBN: " + libro.getIsbn());
        System.out.println("Título: " + libro.getTitulo());
        System.out.println("Autor: " + libro.getAutor());
        System.out.println("Estado: " + libro.getEstado());
    }
    
    // Métodos para la gestión de préstamos
    
    /**
     * Realiza un préstamo de un libro
     */
    private void realizarPrestamo() {
        System.out.println("\n----- Realizar Préstamo -----");
        System.out.print("ISBN del libro: ");
        String isbn = scanner.nextLine();
        
        Libro libro = catalogo.buscarPorIsbn(isbn);
        
        if (libro == null) {
            System.out.println("No se encontró ningún libro con el ISBN: " + isbn);
            return;
        }
        
        if (libro.getEstado() == Estado.PRESTADO) {
            System.out.println("El libro ya está prestado.");
            return;
        }
        
        System.out.println("Duración del préstamo (días, 0 para usar duración predeterminada): ");
        int dias = obtenerEntero();
        
        Prestamo prestamo;
        
        if (dias <= 0) {
            prestamo = sistemaPrestamos.prestarLibro(isbn);
        } else {
            prestamo = sistemaPrestamos.prestarLibro(isbn, dias);
        }
        
        if (prestamo != null) {
            System.out.println("Préstamo realizado correctamente.");
            System.out.println("Fecha de devolución: " + prestamo.getFechaDevolucion().format(dateFormatter));
        } else {
            System.out.println("No se pudo realizar el préstamo.");
        }
    }
    
    /**
     * Devuelve un libro prestado
     */
    private void devolverLibro() {
        System.out.println("\n----- Devolver Libro -----");
        System.out.print("ISBN del libro: ");
        String isbn = scanner.nextLine();
        
        Libro libro = catalogo.buscarPorIsbn(isbn);
        
        if (libro == null) {
            System.out.println("No se encontró ningún libro con el ISBN: " + isbn);
            return;
        }
        
        if (libro.getEstado() == Estado.DISPONIBLE) {
            System.out.println("El libro no está prestado.");
            return;
        }
        
        boolean resultado = sistemaPrestamos.devolverLibro(isbn);
        
        if (resultado) {
            System.out.println("Libro devuelto correctamente.");
        } else {
            System.out.println("No se pudo devolver el libro.");
        }
    }
    
    /**
     * Extiende la duración de un préstamo
     */
    private void extenderPrestamo() {
        System.out.println("\n----- Extender Préstamo -----");
        System.out.print("ISBN del libro: ");
        String isbn = scanner.nextLine();
        
        if (!sistemaPrestamos.libroEstaPrestado(isbn)) {
            System.out.println("El libro no está prestado o no existe.");
            return;
        }
        
        System.out.print("Días de extensión: ");
        int dias = obtenerEntero();
        
        boolean resultado = sistemaPrestamos.extenderPrestamo(isbn, dias);
        
        if (resultado) {
            System.out.println("Préstamo extendido correctamente.");
        } else {
            System.out.println("No se pudo extender el préstamo.");
        }
    }
    
    /**
     * Muestra los préstamos activos
     */
    private void mostrarPrestamosActivos() {
        List<Prestamo> prestamos = sistemaPrestamos.obtenerPrestamosActivos();
        
        if (!prestamos.isEmpty()) {
            System.out.println("\n----- Préstamos Activos -----");
            for (Prestamo prestamo : prestamos) {
                mostrarDetallesPrestamo(prestamo);
                System.out.println("----------------------------");
            }
        } else {
            System.out.println("No hay préstamos activos.");
        }
    }
    
    /**
     * Muestra los préstamos vencidos
     */
    private void mostrarPrestamosVencidos() {
        List<Prestamo> prestamos = sistemaPrestamos.obtenerPrestamosVencidos();
        
        if (!prestamos.isEmpty()) {
            System.out.println("\n----- Préstamos Vencidos -----");
            for (Prestamo prestamo : prestamos) {
                mostrarDetallesPrestamo(prestamo);
                System.out.println("----------------------------");
            }
        } else {
            System.out.println("No hay préstamos vencidos.");
        }
    }
    
    /**
     * Muestra los detalles de un préstamo
     */
    private void mostrarDetallesPrestamo(Prestamo prestamo) {
        Libro libro = prestamo.getLibro();
        System.out.println("Libro: " + libro.getTitulo() + " (" + libro.getIsbn() + ")");
        System.out.println("Autor: " + libro.getAutor());
        System.out.println("Fecha de préstamo: " + prestamo.getFechaPrestamo().format(dateFormatter));
        System.out.println("Fecha de devolución: " + prestamo.getFechaDevolucion().format(dateFormatter));
        
        long diasRestantes = prestamo.calcularDiasRestantes();
        if (diasRestantes < 0) {
            System.out.println("Días de retraso: " + Math.abs(diasRestantes));
        } else {
            System.out.println("Días restantes: " + diasRestantes);
        }
    }
    
    /**
     * Muestra las estadísticas del sistema
     */
    private void mostrarEstadisticas() {
        System.out.println("\n----- Estadísticas -----");
        System.out.println("Total de libros: " + catalogo.contarLibros());
        System.out.println("Libros disponibles: " + catalogo.contarLibrosDisponibles());
        System.out.println("Libros prestados: " + catalogo.contarLibrosPrestados());
        System.out.println("Préstamos activos: " + sistemaPrestamos.contarPrestamosActivos());
        System.out.println("Préstamos vencidos: " + sistemaPrestamos.contarPrestamosVencidos());
    }
    
    /**
     * Obtiene un entero del usuario
     */
    private int obtenerEntero() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
} 