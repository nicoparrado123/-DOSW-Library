package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotAvailableException;
import edu.eci.dosw.tdd.core.exception.BookNotFoundException;
import edu.eci.dosw.tdd.core.exception.LoanLimitException;
import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.core.validator.LoanValidator;
import edu.eci.dosw.tdd.persistence.relational.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.relational.entity.LoanEntity;
import edu.eci.dosw.tdd.persistence.relational.entity.UserEntity;
import edu.eci.dosw.tdd.persistence.relational.repository.LoanRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoanService {

    private static final int LIMITE = 3;
    private final LoanRepository loanRepository;
    private final BookService bookService;
    private final UserService userService;
    private final LoanValidator validator;

    public LoanService(LoanRepository loanRepository, BookService bookService, UserService userService, LoanValidator validator) {
        this.loanRepository = loanRepository;
        this.bookService = bookService;
        this.userService = userService;
        this.validator = validator;
    }

    public Loan prestar(String idUsuario, String idLibro)
            throws UserNotFoundException, BookNotFoundException, BookNotAvailableException, LoanLimitException {
        validator.validar(idUsuario, idLibro);
        UserEntity usuario = userService.buscarEntidadPorId(idUsuario);
        BookEntity libro = bookService.buscarEntidadPorId(idLibro);
        if (libro.getStockDisponible() <= 0) {
            throw new BookNotAvailableException("no hay ejemplares del libro: " + idLibro);
        }
        if (loanRepository.countByUsuarioIdAndEstado(idUsuario, LoanStatus.ACTIVO) >= LIMITE) {
            throw new LoanLimitException("el usuario ya tiene " + LIMITE + " prestamos activos");
        }
        libro.setStockDisponible(libro.getStockDisponible() - 1);
        bookService.actualizarEjemplares(libro.getId(), libro.getStockDisponible());
        LoanEntity entity = loanRepository.save(new LoanEntity(libro, usuario, LocalDate.now()));
        return toModel(entity);
    }

    public Loan devolver(String idUsuario, String idLibro)
            throws UserNotFoundException, BookNotFoundException {
        validator.validar(idUsuario, idLibro);
        userService.buscarEntidadPorId(idUsuario);
        bookService.buscarEntidadPorId(idLibro);
        LoanEntity entity = loanRepository
                .findByUsuarioIdAndLibroIdAndEstado(idUsuario, idLibro, LoanStatus.ACTIVO)
                .orElseThrow(() -> new BookNotFoundException("no hay prestamo activo del libro: " + idLibro));
        entity.setEstado(LoanStatus.DEVUELTO);
        entity.setFechaDevolucion(LocalDate.now());
        bookService.actualizarEjemplares(idLibro, bookService.obtenerEjemplares(idLibro) + 1);
        loanRepository.save(entity);
        return toModel(entity);
    }

    public List<Loan> obtenerTodos() {
        return loanRepository.findAll().stream().map(this::toModel).toList();
    }

    public List<Loan> obtenerPorUsuario(String idUsuario) throws UserNotFoundException {
        userService.buscarEntidadPorId(idUsuario);
        return loanRepository.findByUsuarioId(idUsuario).stream().map(this::toModel).toList();
    }

    private Loan toModel(LoanEntity e) {
        Loan loan = new Loan(
                new edu.eci.dosw.tdd.core.model.Book(e.getLibro().getId(), e.getLibro().getTitulo(), e.getLibro().getAutor(), e.getLibro().getStockDisponible()),
                new edu.eci.dosw.tdd.core.model.User(e.getUsuario().getId(), e.getUsuario().getNombre()),
                e.getFechaPrestamo()
        );
        loan.setEstado(e.getEstado());
        loan.setFechaDevolucion(e.getFechaDevolucion());
        return loan;
    }
}
