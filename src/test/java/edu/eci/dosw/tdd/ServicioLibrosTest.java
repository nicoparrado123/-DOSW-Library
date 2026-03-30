package edu.eci.dosw.tdd;

import edu.eci.dosw.tdd.core.exception.BookNotFoundException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.service.BookService;
import edu.eci.dosw.tdd.core.validator.BookValidator;
import edu.eci.dosw.tdd.persistence.relational.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.relational.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServicioLibrosTest {

    private BookService bookService;
    private BookRepository bookRepository;
    private BookEntity entityNico;

    @BeforeEach
    void iniciar() {
        bookRepository = mock(BookRepository.class);
        bookService = new BookService(bookRepository, new BookValidator());
        entityNico = new BookEntity("nico-001", "clean code", "martin", 3, 3);
        when(bookRepository.findById("nico-001")).thenReturn(Optional.of(entityNico));
        when(bookRepository.findAll()).thenReturn(List.of(entityNico));
    }

    @Test
    void obtenerTodosLosLibros() {
        assertEquals(1, bookService.obtenerTodos().size());
    }

    @Test
    void buscarLibroPorId() throws BookNotFoundException {
        Book libro = bookService.buscarPorId("nico-001");
        assertEquals("nico-001", libro.getId());
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
        verify(bookRepository).save(entityNico);
    }

    @Test
    void agregarLibroNuevo() {
        when(bookRepository.findById("nico-002")).thenReturn(Optional.empty());
        bookService.agregarLibro(new Book("nico-002", "refactoring", "fowler", 0), 2);
        verify(bookRepository).save(any(BookEntity.class));
    }
}
