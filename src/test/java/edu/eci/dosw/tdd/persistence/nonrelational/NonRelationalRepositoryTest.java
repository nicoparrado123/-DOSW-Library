package edu.eci.dosw.tdd.persistence.nonrelational;

import edu.eci.dosw.tdd.persistence.nonrelational.document.BookDocument;
import edu.eci.dosw.tdd.persistence.nonrelational.document.UserDocument;
import edu.eci.dosw.tdd.persistence.nonrelational.document.LoanDocument;
import edu.eci.dosw.tdd.persistence.nonrelational.repository.BookMongoRepository;
import edu.eci.dosw.tdd.persistence.nonrelational.repository.UserMongoRepository;
import edu.eci.dosw.tdd.persistence.nonrelational.repository.LoanMongoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("mongo")
@org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable(named = "CI", matches = "true")
class NonRelationalRepositoryTest {

    @Autowired private BookMongoRepository bookMongoRepository;
    @Autowired private UserMongoRepository userMongoRepository;
    @Autowired private LoanMongoRepository loanMongoRepository;

    @BeforeEach
    void limpiar() {
        loanMongoRepository.deleteAll();
        bookMongoRepository.deleteAll();
        userMongoRepository.deleteAll();
    }

    @Test
    void guardarYBuscarBookMongo() {
        BookDocument doc = new BookDocument();
        doc.setId("mb-001");
        doc.setTitulo("Clean Code");
        doc.setAutor("Martin");
        doc.setCategorias(List.of("programacion"));
        bookMongoRepository.save(doc);
        assertTrue(bookMongoRepository.findById("mb-001").isPresent());
    }

    @Test
    void guardarYBuscarUserMongo() {
        UserDocument doc = new UserDocument();
        doc.setId("mu-001");
        doc.setNombre("Nico");
        doc.setUsername("nico");
        doc.setEmail("nico@mail.com");
        doc.setMembresia("PLATINUM");
        doc.setFechaRegistro(LocalDate.now());
        userMongoRepository.save(doc);
        assertTrue(userMongoRepository.findByUsername("nico").isPresent());
    }

    @Test
    void guardarYBuscarLoanMongo() {
        LoanDocument doc = new LoanDocument();
        doc.setId("ml-001");
        doc.setUsuarioId("mu-001");
        doc.setLibroId("mb-001");
        doc.setFechaPrestamo(LocalDate.now());
        doc.setEstado("ACTIVO");
        doc.setHistorial(List.of(new LoanDocument.HistorialEntry("ACTIVO", LocalDate.now())));
        loanMongoRepository.save(doc);
        assertEquals(1, loanMongoRepository.findByUsuarioId("mu-001").size());
    }
}
