package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.BookNotFoundException;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.validator.BookValidator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookService {

    private final Map<Book, Integer> libros = new HashMap<>();
    private final BookValidator validator;

    public BookService(BookValidator validator) {
        this.validator = validator;
    }

    public void agregarLibro(Book libro, int ejemplares) {
        validator.validar(libro);
        if (libros.containsKey(libro)) {
            libros.put(libro, libros.get(libro) + ejemplares);
        } else {
            libros.put(libro, ejemplares);
        }
    }

    public List<Book> obtenerTodos() {
        return new ArrayList<>(libros.keySet());
    }

    public Book buscarPorId(String id) throws BookNotFoundException {
        for (Book libro : libros.keySet()) {
            if (libro.getId().equals(id)) {
                return libro;
            }
        }
        throw new BookNotFoundException("no existe el libro con id: " + id);
    }

    public int obtenerEjemplares(String id) throws BookNotFoundException {
        return libros.get(buscarPorId(id));
    }

    public void actualizarEjemplares(String id, int cantidad) throws BookNotFoundException {
        libros.put(buscarPorId(id), cantidad);
    }

    public void limpiar() {
        libros.clear();
    }
}
