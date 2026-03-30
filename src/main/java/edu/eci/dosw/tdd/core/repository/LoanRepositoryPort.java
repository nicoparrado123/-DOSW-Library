package edu.eci.dosw.tdd.core.repository;

import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import java.util.List;
import java.util.Optional;

public interface LoanRepositoryPort {
    Loan save(Loan loan);
    List<Loan> findAll();
    List<Loan> findByUsuarioId(String usuarioId);
    long countByUsuarioIdAndEstado(String usuarioId, LoanStatus estado);
    Optional<Loan> findByUsuarioIdAndLibroIdAndEstado(String usuarioId, String libroId, LoanStatus estado);
}
