package edu.eci.dosw.tdd.persistence.relational.impl;

import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.core.repository.LoanRepositoryPort;
import edu.eci.dosw.tdd.persistence.relational.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.relational.entity.LoanEntity;
import edu.eci.dosw.tdd.persistence.relational.entity.UserEntity;
import edu.eci.dosw.tdd.persistence.relational.mapper.LoanEntityMapper;
import edu.eci.dosw.tdd.persistence.relational.repository.BookRepository;
import edu.eci.dosw.tdd.persistence.relational.repository.LoanRepository;
import edu.eci.dosw.tdd.persistence.relational.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("relational")
public class LoanRepositoryJpaImpl implements LoanRepositoryPort {

    private final LoanRepository repository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final LoanEntityMapper mapper;

    public LoanRepositoryJpaImpl(LoanRepository repository, BookRepository bookRepository,
                                  UserRepository userRepository, LoanEntityMapper mapper) {
        this.repository = repository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public Loan save(Loan loan) {
        BookEntity libro = bookRepository.findById(loan.getLibro().getId()).orElseThrow();
        UserEntity usuario = userRepository.findById(loan.getUsuario().getId()).orElseThrow();
        LoanEntity entity = mapper.toEntity(loan, libro, usuario);
        entity.setEstado(loan.getEstado());
        entity.setFechaDevolucion(loan.getFechaDevolucion());
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public List<Loan> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Loan> findByUsuarioId(String usuarioId) {
        return repository.findByUsuarioId(usuarioId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countByUsuarioIdAndEstado(String usuarioId, LoanStatus estado) {
        return repository.countByUsuarioIdAndEstado(usuarioId, estado);
    }

    @Override
    public Optional<Loan> findByUsuarioIdAndLibroIdAndEstado(String usuarioId, String libroId, LoanStatus estado) {
        return repository.findByUsuarioIdAndLibroIdAndEstado(usuarioId, libroId, estado).map(mapper::toDomain);
    }
}
