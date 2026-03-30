package edu.eci.dosw.tdd.persistence.nonrelational.impl;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.repository.LoanRepositoryPort;
import edu.eci.dosw.tdd.persistence.nonrelational.document.LoanDocument;
import edu.eci.dosw.tdd.persistence.nonrelational.mapper.BookDocumentMapper;
import edu.eci.dosw.tdd.persistence.nonrelational.mapper.LoanDocumentMapper;
import edu.eci.dosw.tdd.persistence.nonrelational.mapper.UserDocumentMapper;
import edu.eci.dosw.tdd.persistence.nonrelational.repository.BookMongoRepository;
import edu.eci.dosw.tdd.persistence.nonrelational.repository.LoanMongoRepository;
import edu.eci.dosw.tdd.persistence.nonrelational.repository.UserMongoRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("mongo")
public class LoanRepositoryMongoImpl implements LoanRepositoryPort {

    private final LoanMongoRepository repository;
    private final BookMongoRepository bookRepository;
    private final UserMongoRepository userRepository;
    private final LoanDocumentMapper mapper;
    private final BookDocumentMapper bookMapper;
    private final UserDocumentMapper userMapper;

    public LoanRepositoryMongoImpl(LoanMongoRepository repository, BookMongoRepository bookRepository,
                                    UserMongoRepository userRepository, LoanDocumentMapper mapper,
                                    BookDocumentMapper bookMapper, UserDocumentMapper userMapper) {
        this.repository = repository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
        this.bookMapper = bookMapper;
        this.userMapper = userMapper;
    }

    @Override
    public Loan save(Loan loan) {
        LoanDocument doc = mapper.toDocument(loan);
        return toLoan(repository.save(doc));
    }

    @Override
    public List<Loan> findAll() {
        return repository.findAll().stream().map(this::toLoan).toList();
    }

    @Override
    public List<Loan> findByUsuarioId(String usuarioId) {
        return repository.findByUsuarioId(usuarioId).stream().map(this::toLoan).toList();
    }

    @Override
    public long countByUsuarioIdAndEstado(String usuarioId, LoanStatus estado) {
        return repository.findByUsuarioId(usuarioId).stream()
                .filter(d -> estado.name().equals(d.getEstado()))
                .count();
    }

    @Override
    public Optional<Loan> findByUsuarioIdAndLibroIdAndEstado(String usuarioId, String libroId, LoanStatus estado) {
        return repository.findByUsuarioId(usuarioId).stream()
                .filter(d -> libroId.equals(d.getLibroId()) && estado.name().equals(d.getEstado()))
                .findFirst()
                .map(this::toLoan);
    }

    private Loan toLoan(LoanDocument d) {
        Book libro = bookRepository.findById(d.getLibroId()).map(bookMapper::toDomain)
                .orElse(new Book(d.getLibroId(), "", "", 0));
        User usuario = userRepository.findById(d.getUsuarioId()).map(userMapper::toDomain)
                .orElse(new User(d.getUsuarioId(), ""));
        return mapper.toDomain(d, libro, usuario);
    }
}
