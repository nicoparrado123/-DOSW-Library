package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.core.exception.BookNotFoundException;
import edu.eci.dosw.tdd.core.exception.LoanLimitException;
import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.validator.LoanValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanService {

    private static final int LIMITE = 3;
    private final List<Loan> prestamos = new ArrayList<>();
    private final BookService bookService;
    private final UserService userService;
    private final LoanValidator validator;

    public LoanService(BookService bookService, UserService userService, LoanValidator validator) {
        this.bookService = bookService;
        this.userService = userService;
        this.validator = validator;
    }

    public Loan prestar(String idUsuario, String idLibro)
            throws UserNotFoundException, BookNotFoundException, BookNotAvailableException, LoanLimitException {
        validator.validar(idUsuario, idLibro);
        User usuario = userService.buscarPorId(idUsuario);
        Book libro = bookService.buscarPorId(idLibro);
        if (bookService.obtenerEjemplares(idLibro) <= 0) {
            throw new BookNotAvailableException("no hay ejemplares del libro: " + idLibro);
        }
        int activos = 0;
        for (Loan p : prestamos) {
            if (p.getUsuario().getId().equals(idUsuario) && p.getEstado() == LoanStatus.ACTIVO) {
                activos++;
            }
        }
        if (activos >= LIMITE) {
            throw new LoanLimitException("el usuario ya tiene " + LIMITE + " prestamos activos");
        }
        bookService.actualizarEjemplares(idLibro, bookService.obtenerEjemplares(idLibro) - 1);
        Loan prestamo = new Loan(libro, usuario, LocalDate.now());
        prestamos.add(prestamo);
        return prestamo;
    }

    public Loan devolver(String idUsuario, String idLibro)
            throws UserNotFoundException, BookNotFoundException {
        validator.validar(idUsuario, idLibro);
        userService.buscarPorId(idUsuario);
        bookService.buscarPorId(idLibro);
        for (Loan p : prestamos) {
            if (p.getUsuario().getId().equals(idUsuario)
                    && p.getLibro().getId().equals(idLibro)
                    && p.getEstado() == LoanStatus.ACTIVO) {
                p.setEstado(LoanStatus.DEVUELTO);
                p.setFechaDevolucion(LocalDate.now());
                bookService.actualizarEjemplares(idLibro, bookService.obtenerEjemplares(idLibro) + 1);
                return p;
            }
        }
        throw new BookNotFoundException("no hay prestamo activo del libro: " + idLibro);
    }

    public List<Loan> obtenerTodos() {
        return new ArrayList<>(prestamos);
    }

    public List<Loan> obtenerPorUsuario(String idUsuario) throws UserNotFoundException {
        userService.buscarPorId(idUsuario);
        List<Loan> resultado = new ArrayList<>();
        for (Loan p : prestamos) {
            if (p.getUsuario().getId().equals(idUsuario)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public void limpiar() {
        prestamos.clear();
    }
}
