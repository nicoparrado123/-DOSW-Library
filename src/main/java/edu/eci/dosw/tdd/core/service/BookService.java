package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotFoundException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.repository.BookRepositoryPort;
import edu.eci.dosw.tdd.core.validator.BookValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepositoryPort bookRepository;
    private final BookValidator validator;

    public BookService(BookRepositoryPort bookRepository, BookValidator validator) {
        this.bookRepository = bookRepository;
        this.validator = validator;
    }

    public void agregarLibro(Book libro, int ejemplares) {
        validator.validar(libro);
        if (ejemplares <= 0) throw new IllegalArgumentException("el stock total debe ser mayor a 0");
        bookRepository.save(libro, ejemplares);
    }

    public List<Book> obtenerTodos() {
        return bookRepository.findAll();
    }

    public Book buscarPorId(String id) throws BookNotFoundException {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("no existe el libro con id: " + id));
    }

    public int obtenerEjemplares(String id) throws BookNotFoundException {
        buscarPorId(id);
        return bookRepository.getStock(id);
    }

    public void actualizarEjemplares(String id, int cantidad) throws BookNotFoundException {
        Book libro = buscarPorId(id);
        if (cantidad < 0) throw new IllegalArgumentException("la cantidad disponible no puede ser negativa");
        bookRepository.updateStock(id, cantidad);
    }
}
