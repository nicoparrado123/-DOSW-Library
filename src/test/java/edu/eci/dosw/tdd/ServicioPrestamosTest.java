package edu.eci.dosw.tdd;

import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.core.exception.BookNotFoundException;
import edu.eci.dosw.tdd.core.exception.LoanLimitException;
import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.service.BookService;
import edu.eci.dosw.tdd.core.service.LoanService;
import edu.eci.dosw.tdd.core.service.UserService;
import edu.eci.dosw.tdd.core.validator.BookValidator;
import edu.eci.dosw.tdd.core.validator.LoanValidator;
import edu.eci.dosw.tdd.core.validator.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServicioPrestamosTest {

    private LoanService loanService;
    private BookService bookService;
    private UserService userService;

    @BeforeEach
    void iniciar() {
        bookService = new BookService(new BookValidator());
        userService = new UserService(new UserValidator());
        loanService = new LoanService(bookService, userService, new LoanValidator());

        userService.registrar(new User("nico-001", "nico"));
        bookService.agregarLibro(new Book("nico-l1", "clean code", "martin"), 2);
        bookService.agregarLibro(new Book("nico-l2", "refactoring", "fowler"), 1);
        bookService.agregarLibro(new Book("nico-l3", "ddd", "evans"), 1);
        bookService.agregarLibro(new Book("nico-l4", "sicp", "abelson"), 1);
    }

    @Test
    void prestarLibroExitoso() throws Exception {
        Loan prestamo = loanService.prestar("nico-001", "nico-l1");
        assertEquals(LoanStatus.ACTIVO, prestamo.getEstado());
    }

    @Test
    void prestarLibroReduceEjemplares() throws Exception {
        loanService.prestar("nico-001", "nico-l1");
        assertEquals(1, bookService.obtenerEjemplares("nico-l1"));
    }

    @Test
    void devolverLibroExitoso() throws Exception {
        loanService.prestar("nico-001", "nico-l1");
        Loan prestamo = loanService.devolver("nico-001", "nico-l1");
        assertEquals(LoanStatus.DEVUELTO, prestamo.getEstado());
        assertNotNull(prestamo.getFechaDevolucion());
    }

    @Test
    void devolverLibroRestaureaEjemplares() throws Exception {
        loanService.prestar("nico-001", "nico-l1");
        loanService.devolver("nico-001", "nico-l1");
        assertEquals(2, bookService.obtenerEjemplares("nico-l1"));
    }

    @Test
    void prestarSinEjemplaresLanzaExcepcion() throws Exception {
        loanService.prestar("nico-001", "nico-l2");
        assertThrows(BookNotAvailableException.class, () -> loanService.prestar("nico-001", "nico-l2"));
    }

    @Test
    void prestarSuperaLimiteLanzaExcepcion() throws Exception {
        loanService.prestar("nico-001", "nico-l1");
        loanService.prestar("nico-001", "nico-l2");
        loanService.prestar("nico-001", "nico-l3");
        assertThrows(LoanLimitException.class, () -> loanService.prestar("nico-001", "nico-l4"));
    }

    @Test
    void prestarUsuarioInexistenteLanzaExcepcion() {
        assertThrows(UserNotFoundException.class, () -> loanService.prestar("nico-999", "nico-l1"));
    }

    @Test
    void prestarLibroInexistenteLanzaExcepcion() {
        assertThrows(BookNotFoundException.class, () -> loanService.prestar("nico-001", "nico-999"));
    }

    @Test
    void obtenerPrestamosPorUsuario() throws Exception {
        loanService.prestar("nico-001", "nico-l1");
        assertEquals(1, loanService.obtenerPorUsuario("nico-001").size());
    }

    @Test
    void obtenerTodosLosPrestamos() throws Exception {
        loanService.prestar("nico-001", "nico-l1");
        assertEquals(1, loanService.obtenerTodos().size());
    }
}
