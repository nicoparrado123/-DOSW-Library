package edu.eci.dosw.tdd.controller.tdd;

import edu.eci.dosw.tdd.persistence.nonrelational.document.BookDocument;
import edu.eci.dosw.tdd.persistence.nonrelational.document.UserDocument;
import edu.eci.dosw.tdd.persistence.nonrelational.repository.BookMongoRepository;
import edu.eci.dosw.tdd.persistence.nonrelational.repository.LoanMongoRepository;
import edu.eci.dosw.tdd.persistence.nonrelational.repository.UserMongoRepository;
import edu.eci.dosw.tdd.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("mongo")
public class LoanTDD {

    @Autowired private MockMvc mockMvc;
    @Autowired private BookMongoRepository bookMongoRepository;
    @Autowired private UserMongoRepository userMongoRepository;
    @Autowired private LoanMongoRepository loanMongoRepository;
    @Autowired private JwtService jwtService;

    private String librarianToken;
    private String userToken;

    @BeforeEach
    void preparar() {
        loanMongoRepository.deleteAll();
        bookMongoRepository.deleteAll();
        userMongoRepository.deleteAll();

        UserDocument user = new UserDocument();
        user.setId("usr-001"); user.setNombre("Nico"); user.setUsername("nico");
        user.setPassword("pass"); user.setRole("USER");
        userMongoRepository.save(user);

        bookMongoRepository.save(buildBook("lib-001", "Clean Code", "Martin", 2));
        bookMongoRepository.save(buildBook("lib-002", "Refactoring", "Fowler", 1));

        librarianToken = jwtService.generateToken("lib-001", "LIBRARIAN");
        userToken = jwtService.generateToken("usr-001", "USER");
    }

    private BookDocument buildBook(String id, String titulo, String autor, int stock) {
        BookDocument doc = new BookDocument();
        doc.setId(id); doc.setTitulo(titulo); doc.setAutor(autor);
        BookDocument.Disponibilidad disp = new BookDocument.Disponibilidad();
        disp.setTotalCopias(stock); disp.setCopiasDisponibles(stock);
        disp.setStatus("DISPONIBLE");
        doc.setDisponibilidad(disp);
        return doc;
    }

    @Test
    void prestarLibroExitoso() throws Exception {
        mockMvc.perform(post("/prestamos/usr-001/lib-001")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    @Test
    void devolverLibroExitoso() throws Exception {
        mockMvc.perform(post("/prestamos/usr-001/lib-001")
                .header("Authorization", "Bearer " + userToken));
        mockMvc.perform(put("/prestamos/devolver/usr-001/lib-001")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("DEVUELTO"));
    }

    @Test
    void prestarLibroSinEjemplaresRetorna409() throws Exception {
        mockMvc.perform(post("/prestamos/usr-001/lib-002")
                .header("Authorization", "Bearer " + userToken));
        mockMvc.perform(post("/prestamos/usr-001/lib-002")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void prestarUsuarioInexistenteRetorna404() throws Exception {
        mockMvc.perform(post("/prestamos/no-existe/lib-001")
                .header("Authorization", "Bearer " + librarianToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void obtenerTodosLosPrestamos() throws Exception {
        mockMvc.perform(post("/prestamos/usr-001/lib-001")
                .header("Authorization", "Bearer " + userToken));
        mockMvc.perform(get("/prestamos")
                .header("Authorization", "Bearer " + librarianToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
