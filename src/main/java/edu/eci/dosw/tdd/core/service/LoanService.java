package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.core.exception.BookNotFoundException;
import edu.eci.dosw.tdd.core.exception.LoanLimitException;
import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.repository.LoanRepositoryPort;
import edu.eci.dosw.tdd.core.validator.LoanValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {

    private static final int LIMITE = 3;
    private final LoanRepositoryPort loanRepository;
    private final BookService bookService;
    private final UserService userService;
    private final LoanValidator validator;

    public LoanService(LoanRepositoryPort loanRepository, BookService bookService,
                       UserService userService, LoanValidator validator) {
        this.loanRepository = loanRepository;
        this.bookService = bookService;
        this.userService = userService;
        this.validator = validator;
    }

    public Loan prestar(String idUsuario, String idLibro)
            throws UserNotFoundException, BookNotFoundException, BookNotAvailableException, LoanLimitException {
        validator.validar(idUsuario, idLibro);
        User usuario = userService.buscarEntidadPorId(idUsuario);
        Book libro = bookService.buscarPorId(idLibro);
        if (bookService.obtenerEjemplares(idLibro) <= 0) {
            throw new BookNotAvailableException("no hay ejemplares del libro: " + idLibro);
        }
        if (loanRepository.countByUsuarioIdAndEstado(idUsuario, LoanStatus.ACTIVO) >= LIMITE) {
            throw new LoanLimitException("el usuario ya tiene " + LIMITE + " prestamos activos");
        }
        bookService.actualizarEjemplares(idLibro, bookService.obtenerEjemplares(idLibro) - 1);
        Loan loan = new Loan(libro, usuario, LocalDate.now());
        return loanRepository.save(loan);
    }

    public Loan devolver(String idUsuario, String idLibro)
            throws UserNotFoundException, BookNotFoundException {
        validator.validar(idUsuario, idLibro);
        userService.buscarEntidadPorId(idUsuario);
        bookService.buscarPorId(idLibro);
        Loan loan = loanRepository.findByUsuarioIdAndLibroIdAndEstado(idUsuario, idLibro, LoanStatus.ACTIVO)
                .orElseThrow(() -> new BookNotFoundException("no hay prestamo activo del libro: " + idLibro));
        loan.setEstado(LoanStatus.DEVUELTO);
        loan.setFechaDevolucion(LocalDate.now());
        bookService.actualizarEjemplares(idLibro, bookService.obtenerEjemplares(idLibro) + 1);
        return loanRepository.save(loan);
    }

    public List<Loan> obtenerTodos() {
        return loanRepository.findAll();
    }

    public List<Loan> obtenerPorUsuario(String idUsuario) throws UserNotFoundException {
        userService.buscarEntidadPorId(idUsuario);
        return loanRepository.findByUsuarioId(idUsuario);
    }
}
