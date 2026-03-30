package edu.eci.dosw.tdd.persistence.relational;

import edu.eci.dosw.tdd.persistence.relational.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.relational.entity.UserEntity;
import edu.eci.dosw.tdd.persistence.relational.entity.LoanEntity;
import edu.eci.dosw.tdd.persistence.relational.repository.BookRepository;
import edu.eci.dosw.tdd.persistence.relational.repository.UserRepository;
import edu.eci.dosw.tdd.persistence.relational.repository.LoanRepository;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RelationalRepositoryTest {

    @Autowired private BookRepository bookRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private LoanRepository loanRepository;

    @BeforeEach
    void limpiar() {
        loanRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void guardarYBuscarBook() {
        bookRepository.save(new BookEntity("b-001", "Clean Code", "Martin", 3, 3));
        assertTrue(bookRepository.findById("b-001").isPresent());
    }

    @Test
    void guardarYBuscarUser() {
        userRepository.save(new UserEntity("u-001", "Nico", "nico", "pass", UserEntity.Role.USER));
        assertTrue(userRepository.findByUsername("nico").isPresent());
    }

    @Test
    void guardarYContarLoanActivo() {
        UserEntity user = userRepository.save(new UserEntity("u-002", "Ana", "ana", "pass", UserEntity.Role.USER));
        BookEntity book = bookRepository.save(new BookEntity("b-002", "Refactoring", "Fowler", 2, 2));
        loanRepository.save(new LoanEntity(book, user, LocalDate.now()));
        assertEquals(1, loanRepository.countByUsuarioIdAndEstado("u-002", LoanStatus.ACTIVO));
    }
}
