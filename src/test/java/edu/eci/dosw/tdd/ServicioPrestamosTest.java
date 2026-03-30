package edu.eci.dosw.tdd;

import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.core.exception.BookNotFoundException;
import edu.eci.dosw.tdd.core.exception.LoanLimitException;
import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.core.service.BookService;
import edu.eci.dosw.tdd.core.service.LoanService;
import edu.eci.dosw.tdd.core.service.UserService;
import edu.eci.dosw.tdd.core.validator.LoanValidator;
import edu.eci.dosw.tdd.persistence.relational.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.relational.entity.LoanEntity;
import edu.eci.dosw.tdd.persistence.relational.entity.UserEntity;
import edu.eci.dosw.tdd.persistence.relational.repository.LoanRepository;
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
    private LoanRepository loanRepository;

    private BookEntity bookL1;
    private BookEntity bookL2;
    private UserEntity userNico;

    @BeforeEach
    void iniciar() throws Exception {
        bookService = mock(BookService.class);
        userService = mock(UserService.class);
        loanRepository = mock(LoanRepository.class);
        loanService = new LoanService(loanRepository, bookService, userService, new LoanValidator());

        userNico = new UserEntity("nico-001", "nico", "nico", "pass", UserEntity.Role.USER);
        bookL1 = new BookEntity("nico-l1", "clean code", "martin", 2, 2);
        bookL2 = new BookEntity("nico-l2", "refactoring", "fowler", 1, 1);

        when(userService.buscarEntidadPorId("nico-001")).thenReturn(userNico);
        when(bookService.buscarEntidadPorId("nico-l1")).thenReturn(bookL1);
        when(bookService.buscarEntidadPorId("nico-l2")).thenReturn(bookL2);
        when(loanRepository.countByUsuarioIdAndEstado("nico-001", LoanStatus.ACTIVO)).thenReturn(0L);
        when(loanRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void prestarLibroExitoso() throws Exception {
        var prestamo = loanService.prestar("nico-001", "nico-l1");
        assertEquals(LoanStatus.ACTIVO, prestamo.getEstado());
    }

    @Test
    void prestarLibroReduceEjemplares() throws Exception {
        loanService.prestar("nico-001", "nico-l1");
        verify(bookService).actualizarEjemplares("nico-l1", 1);
    }

    @Test
    void prestarSinEjemplaresLanzaExcepcion() {
        bookL2.setStockDisponible(0);
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
        when(bookService.buscarEntidadPorId("nico-999")).thenThrow(new BookNotFoundException("no existe"));
        assertThrows(BookNotFoundException.class, () -> loanService.prestar("nico-001", "nico-999"));
    }

    @Test
    void devolverLibroExitoso() throws Exception {
        LoanEntity entity = new LoanEntity(bookL1, userNico, LocalDate.now());
        when(loanRepository.findByUsuarioIdAndLibroIdAndEstado("nico-001", "nico-l1", LoanStatus.ACTIVO))
                .thenReturn(Optional.of(entity));
        when(bookService.obtenerEjemplares("nico-l1")).thenReturn(1);
        var prestamo = loanService.devolver("nico-001", "nico-l1");
        assertEquals(LoanStatus.DEVUELTO, prestamo.getEstado());
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
