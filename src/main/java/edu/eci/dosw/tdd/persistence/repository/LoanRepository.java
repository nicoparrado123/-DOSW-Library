package edu.eci.dosw.tdd.persistence.repository;

import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.persistence.entity.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<LoanEntity, Long> {
    List<LoanEntity> findByUsuarioId(String userId);
    long countByUsuarioIdAndEstado(String userId, LoanStatus estado);
    Optional<LoanEntity> findByUsuarioIdAndLibroIdAndEstado(String userId, String bookId, LoanStatus estado);
}
