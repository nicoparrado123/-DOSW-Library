package edu.eci.dosw.tdd;

import edu.eci.dosw.tdd.core.exception.BookNotFoundException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.service.BookService;
import edu.eci.dosw.tdd.core.validator.BookValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServicioLibrosTest {

    private BookService bookService;
    private Book libroNico;

    @BeforeEach
    void iniciar() {
        bookService = new BookService(new BookValidator());
        libroNico = new Book("nico-001", "clean code", "martin");
        bookService.agregarLibro(libroNico, 3);
    }

    @Test
    void obtenerTodosLosLibros() {
        assertEquals(1, bookService.obtenerTodos().size());
    }

    @Test
    void buscarLibroPorId() throws BookNotFoundException {
        assertEquals(libroNico, bookService.buscarPorId("nico-001"));
    }

    @Test
    void buscarLibroIdInexistente() {
        assertThrows(BookNotFoundException.class, () -> bookService.buscarPorId("nico-999"));
    }

    @Test
    void obtenerEjemplares() throws BookNotFoundException {
        assertEquals(3, bookService.obtenerEjemplares("nico-001"));
    }

    @Test
    void actualizarEjemplares() throws BookNotFoundException {
        bookService.actualizarEjemplares("nico-001", 5);
        assertEquals(5, bookService.obtenerEjemplares("nico-001"));
    }

    @Test
    void agregarLibroAcumulaEjemplares() throws BookNotFoundException {
        bookService.agregarLibro(libroNico, 2);
        assertEquals(5, bookService.obtenerEjemplares("nico-001"));
    }
}
