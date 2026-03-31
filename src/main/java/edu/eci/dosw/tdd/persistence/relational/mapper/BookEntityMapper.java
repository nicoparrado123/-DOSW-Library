package edu.eci.dosw.tdd.persistence.relational.mapper;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.persistence.relational.entity.BookEntity;
import org.springframework.stereotype.Component;

@Component
public class BookEntityMapper {
    public Book toDomain(BookEntity e) {
        return new Book(e.getId(), e.getTitulo(), e.getAutor(), e.getStockDisponible());
    }
    public BookEntity toEntity(Book b, int stockTotal, int stockDisponible) {
        return new BookEntity(b.getId(), b.getTitulo(), b.getAutor(), stockTotal, stockDisponible);
    }
}
