package edu.eci.dosw.tdd;

import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.core.exception.BookNotFoundException;
import edu.eci.dosw.tdd.core.exception.LoanLimitException;
import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.repository.LoanRepositoryPort;
import edu.eci.dosw.tdd.core.service.BookService;
import edu.eci.dosw.tdd.core.service.LoanService;
import edu.eci.dosw.tdd.core.service.UserService;
import edu.eci.dosw.tdd.core.validator.LoanValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServicioPrestamosTest {

    private LoanService loanService;
    private BookService bookService;
    private UserService userService;
    private LoanRepositoryPort loanRepository;

    private User userNico;
    private Book bookL1;
    private Book bookL2;

    @BeforeEach
    void iniciar() throws Exception {
        bookService = mock(BookService.class);
        userService = mock(UserService.class);
        loanRepository = mock(LoanRepositoryPort.class);
        loanService = new LoanService(loanRepository, bookService, userService, new LoanValidator());

        userNico = new User("nico-001", "nico");
        bookL1 = new Book("nico-l1", "clean code", "martin", 2);
        bookL2 = new Book("nico-l2", "refactoring", "fowler", 1);

        when(userService.buscarEntidadPorId("nico-001")).thenReturn(userNico);
        when(bookService.buscarPorId("nico-l1")).thenReturn(bookL1);
        when(bookService.buscarPorId("nico-l2")).thenReturn(bookL2);
        when(bookService.obtenerEjemplares("nico-l1")).thenReturn(2);
        when(bookService.obtenerEjemplares("nico-l2")).thenReturn(1);
        when(loanRepository.countByUsuarioIdAndEstado("nico-001", LoanStatus.ACTIVO)).thenReturn(0L);
        when(loanRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void prestarLibroExitoso() throws Exception {
        Loan prestamo = loanService.prestar("nico-001", "nico-l1");
        assertEquals(LoanStatus.ACTIVO, prestamo.getEstado());
    }

    @Test
    void prestarLibroReduceEjemplares() throws Exception {
        loanService.prestar("nico-001", "nico-l1");
        verify(bookService).actualizarEjemplares("nico-l1", 1);
    }

    @Test
    void prestarSinEjemplaresLanzaExcepcion() throws Exception {
        when(bookService.obtenerEjemplares("nico-l2")).thenReturn(0);
        assertThrows(BookNotAvailableException.class, () -> loanService.prestar("nico-001", "nico-l2"));
    }

    @Test
    void prestarSuperaLimiteLanzaExcepcion() {
        when(loanRepository.countByUsuarioIdAndEstado("nico-001", LoanStatus.ACTIVO)).thenReturn(3L);
        assertThrows(LoanLimitException.class, () -> loanService.prestar("nico-001", "nico-l1"));
    }

    @Test
    void prestarUsuarioInexistenteLanzaExcepcion() throws Exception {
        when(userService.buscarEntidadPorId("nico-999")).thenThrow(new UserNotFoundException("no existe"));
        assertThrows(UserNotFoundException.class, () -> loanService.prestar("nico-999", "nico-l1"));
    }

    @Test
    void prestarLibroInexistenteLanzaExcepcion() throws Exception {
        when(bookService.buscarPorId("nico-999")).thenThrow(new BookNotFoundException("no existe"));
        assertThrows(BookNotFoundException.class, () -> loanService.prestar("nico-001", "nico-999"));
    }

    @Test
    void devolverLibroExitoso() throws Exception {
        Loan loan = new Loan(bookL1, userNico, LocalDate.now());
        when(loanRepository.findByUsuarioIdAndLibroIdAndEstado("nico-001", "nico-l1", LoanStatus.ACTIVO))
                .thenReturn(Optional.of(loan));
        when(bookService.obtenerEjemplares("nico-l1")).thenReturn(1);
        when(loanRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        Loan resultado = loanService.devolver("nico-001", "nico-l1");
        assertEquals(LoanStatus.DEVUELTO, resultado.getEstado());
    }

    @Test
    void obtenerPrestamosPorUsuario() throws Exception {
        when(loanRepository.findByUsuarioId("nico-001")).thenReturn(List.of());
        assertEquals(0, loanService.obtenerPorUsuario("nico-001").size());
    }

    @Test
    void obtenerTodosLosPrestamos() {
        when(loanRepository.findAll()).thenReturn(List.of());
        assertEquals(0, loanService.obtenerTodos().size());
    }
}
