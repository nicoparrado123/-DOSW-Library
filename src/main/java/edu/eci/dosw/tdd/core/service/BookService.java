package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotFoundException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.validator.BookValidator;
import edu.eci.dosw.tdd.persistence.relational.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.relational.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookValidator validator;

    public BookService(BookRepository bookRepository, BookValidator validator) {
        this.bookRepository = bookRepository;
        this.validator = validator;
    }

    public void agregarLibro(Book libro, int ejemplares) {
        validator.validar(libro);
        if (ejemplares <= 0) throw new IllegalArgumentException("el stock total debe ser mayor a 0");
        BookEntity entity = bookRepository.findById(libro.getId())
                .orElse(new BookEntity(libro.getId(), libro.getTitulo(), libro.getAutor(), 0, 0));
        entity.setStockTotal(entity.getStockTotal() + ejemplares);
        entity.setStockDisponible(entity.getStockDisponible() + ejemplares);
        bookRepository.save(entity);
    }

    public List<Book> obtenerTodos() {
        return bookRepository.findAll().stream()
                .map(e -> new Book(e.getId(), e.getTitulo(), e.getAutor(), e.getStockDisponible()))
                .toList();
    }

    public Book buscarPorId(String id) throws BookNotFoundException {
        BookEntity entity = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("no existe el libro con id: " + id));
        return new Book(entity.getId(), entity.getTitulo(), entity.getAutor(), entity.getStockDisponible());
    }

    public int obtenerEjemplares(String id) throws BookNotFoundException {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("no existe el libro con id: " + id))
                .getStockDisponible();
    }

    public void actualizarEjemplares(String id, int cantidad) throws BookNotFoundException {
        BookEntity entity = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("no existe el libro con id: " + id));
        if (cantidad < 0) throw new IllegalArgumentException("la cantidad disponible no puede ser negativa");
        if (cantidad > entity.getStockTotal()) throw new IllegalArgumentException("no puede superar el stock original");
        entity.setStockDisponible(cantidad);
        bookRepository.save(entity);
    }

    public BookEntity buscarEntidadPorId(String id) throws BookNotFoundException {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("no existe el libro con id: " + id));
    }
}
