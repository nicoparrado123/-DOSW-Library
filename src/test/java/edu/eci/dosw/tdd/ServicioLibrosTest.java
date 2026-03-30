package edu.eci.dosw.tdd;

import edu.eci.dosw.tdd.core.exception.BookNotFoundException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.repository.BookRepositoryPort;
import edu.eci.dosw.tdd.core.service.BookService;
import edu.eci.dosw.tdd.core.validator.BookValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServicioLibrosTest {

    private BookService bookService;
    private BookRepositoryPort bookRepository;

    @BeforeEach
    void iniciar() {
        bookRepository = mock(BookRepositoryPort.class);
        bookService = new BookService(bookRepository, new BookValidator());
        Book book = new Book("nico-001", "clean code", "martin", 3);
        when(bookRepository.findById("nico-001")).thenReturn(Optional.of(book));
        when(bookRepository.findAll()).thenReturn(List.of(book));
        when(bookRepository.getStock("nico-001")).thenReturn(3);
    }

    @Test
    void obtenerTodosLosLibros() {
        assertEquals(1, bookService.obtenerTodos().size());
    }

    @Test
    void buscarLibroPorId() throws BookNotFoundException {
        assertEquals("nico-001", bookService.buscarPorId("nico-001").getId());
    }

    @Test
    void buscarLibroIdInexistente() {
        when(bookRepository.findById("nico-999")).thenReturn(Optional.empty());
        assertThrows(BookNotFoundException.class, () -> bookService.buscarPorId("nico-999"));
    }

    @Test
    void obtenerEjemplares() throws BookNotFoundException {
        assertEquals(3, bookService.obtenerEjemplares("nico-001"));
    }

    @Test
    void actualizarEjemplares() throws BookNotFoundException {
        bookService.actualizarEjemplares("nico-001", 2);
        verify(bookRepository).updateStock("nico-001", 2);
    }

    @Test
    void agregarLibroNuevo() {
        when(bookRepository.findById("nico-002")).thenReturn(Optional.empty());
        when(bookRepository.save(any(), anyInt())).thenReturn(new Book("nico-002", "refactoring", "fowler", 2));
        bookService.agregarLibro(new Book("nico-002", "refactoring", "fowler", 0), 2);
        verify(bookRepository).save(any(Book.class), eq(2));
    }
}
