package edu.eci.dosw.tdd.persistence.relational.impl;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.repository.BookRepositoryPort;
import edu.eci.dosw.tdd.persistence.relational.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.relational.mapper.BookEntityMapper;
import edu.eci.dosw.tdd.persistence.relational.repository.BookRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("relational")
public class BookRepositoryJpaImpl implements BookRepositoryPort {

    private final BookRepository repository;
    private final BookEntityMapper mapper;

    public BookRepositoryJpaImpl(BookRepository repository, BookEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Book save(Book book, int ejemplares) {
        BookEntity entity = repository.findById(book.getId())
                .orElse(mapper.toEntity(book, 0, 0));
        entity.setStockTotal(entity.getStockTotal() + ejemplares);
        entity.setStockDisponible(entity.getStockDisponible() + ejemplares);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Book> findById(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Book> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public int getStock(String id) {
        return repository.findById(id).map(BookEntity::getStockDisponible).orElse(0);
    }

    @Override
    public void updateStock(String id, int cantidad) {
        repository.findById(id).ifPresent(e -> {
            e.setStockDisponible(cantidad);
            repository.save(e);
        });
    }
}
