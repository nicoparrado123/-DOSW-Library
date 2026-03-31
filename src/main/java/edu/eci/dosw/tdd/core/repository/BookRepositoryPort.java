package edu.eci.dosw.tdd.core.repository;

import edu.eci.dosw.tdd.core.model.Book;
import java.util.List;
import java.util.Optional;

public interface BookRepositoryPort {
    Book save(Book book, int ejemplares);
    Optional<Book> findById(String id);
    List<Book> findAll();
    int getStock(String id);
    void updateStock(String id, int cantidad);
}
