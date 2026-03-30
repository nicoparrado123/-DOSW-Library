package edu.eci.dosw.tdd.persistence.relational.mapper;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.persistence.relational.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.relational.entity.LoanEntity;
import edu.eci.dosw.tdd.persistence.relational.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class LoanEntityMapper {
    public Loan toDomain(LoanEntity e) {
        Loan loan = new Loan(
                new Book(e.getLibro().getId(), e.getLibro().getTitulo(), e.getLibro().getAutor(), e.getLibro().getStockDisponible()),
                new User(e.getUsuario().getId(), e.getUsuario().getNombre()),
                e.getFechaPrestamo()
        );
        loan.setEstado(e.getEstado());
        loan.setFechaDevolucion(e.getFechaDevolucion());
        return loan;
    }
    public LoanEntity toEntity(Loan loan, BookEntity libro, UserEntity usuario) {
        return new LoanEntity(libro, usuario, loan.getFechaPrestamo());
    }
}
