package edu.eci.dosw.tdd.controller.tdd;

import edu.eci.dosw.tdd.persistence.relational.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.relational.entity.UserEntity;
import edu.eci.dosw.tdd.persistence.relational.repository.BookRepository;
import edu.eci.dosw.tdd.persistence.relational.repository.LoanRepository;
import edu.eci.dosw.tdd.persistence.relational.repository.UserRepository;
import edu.eci.dosw.tdd.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LoanTDD {

    @Autowired private MockMvc mockMvc;
    @Autowired private BookRepository bookRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private LoanRepository loanRepository;
    @Autowired private JwtService jwtService;

    private String librarianToken;
    private String userToken;

    @BeforeEach
    void preparar() {
        loanRepository.deleteAll();
        bookRepository.deleteAll();
        userRepository.deleteAll();
        userRepository.save(new UserEntity("usr-001", "Nico", "nico", "pass", UserEntity.Role.USER));
        bookRepository.save(new BookEntity("lib-001", "Clean Code", "Martin", 2, 2));
        bookRepository.save(new BookEntity("lib-002", "Refactoring", "Fowler", 1, 1));
        librarianToken = jwtService.generateToken("lib-001", "LIBRARIAN");
        userToken = jwtService.generateToken("usr-001", "USER");
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
