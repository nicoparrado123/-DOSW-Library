package edu.eci.dosw.tdd.persistence.nonrelational.impl;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.repository.BookRepositoryPort;
import edu.eci.dosw.tdd.persistence.nonrelational.document.BookDocument;
import edu.eci.dosw.tdd.persistence.nonrelational.mapper.BookDocumentMapper;
import edu.eci.dosw.tdd.persistence.nonrelational.repository.BookMongoRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("mongo")
public class BookRepositoryMongoImpl implements BookRepositoryPort {

    private final BookMongoRepository repository;
    private final BookDocumentMapper mapper;

    public BookRepositoryMongoImpl(BookMongoRepository repository, BookDocumentMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Book save(Book book, int ejemplares) {
        BookDocument doc = repository.findById(book.getId())
                .orElse(mapper.toDocument(book, 0, 0));
        BookDocument.Disponibilidad disp = doc.getDisponibilidad() != null
                ? doc.getDisponibilidad() : new BookDocument.Disponibilidad();
        disp.setTotalCopias(disp.getTotalCopias() + ejemplares);
        disp.setCopiasDisponibles(disp.getCopiasDisponibles() + ejemplares);
        disp.setStatus("DISPONIBLE");
        doc.setDisponibilidad(disp);
        return mapper.toDomain(repository.save(doc));
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
        return repository.findById(id)
                .map(d -> d.getDisponibilidad() != null ? d.getDisponibilidad().getCopiasDisponibles() : 0)
                .orElse(0);
    }

    @Override
    public void updateStock(String id, int cantidad) {
        repository.findById(id).ifPresent(d -> {
            d.getDisponibilidad().setCopiasDisponibles(cantidad);
            d.getDisponibilidad().setCopiasPrestadas(d.getDisponibilidad().getTotalCopias() - cantidad);
            d.getDisponibilidad().setStatus(cantidad > 0 ? "DISPONIBLE" : "NO_DISPONIBLE");
            repository.save(d);
        });
    }
}
