package edu.eci.dosw.tdd.controller.tdd;

import edu.eci.dosw.tdd.persistence.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.entity.UserEntity;
import edu.eci.dosw.tdd.persistence.repository.BookRepository;
import edu.eci.dosw.tdd.persistence.repository.LoanRepository;
import edu.eci.dosw.tdd.persistence.repository.UserRepository;
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
public class ExtraControllerTDD {

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
        bookRepository.save(new BookEntity("lib-001", "Clean Code", "Martin", 3, 3));
        bookRepository.save(new BookEntity("lib-002", "Refactoring", "Fowler", 1, 1));
        bookRepository.save(new BookEntity("lib-003", "DDD", "Evans", 1, 1));
        bookRepository.save(new BookEntity("lib-004", "SICP", "Abelson", 1, 1));
        librarianToken = jwtService.generateToken("lib-001", "LIBRARIAN");
        userToken = jwtService.generateToken("usr-001", "USER");
    }

    @Test
    void prestarCuandoYaTiene3ActivosRetorna409() throws Exception {
        mockMvc.perform(post("/prestamos/usr-001/lib-001").header("Authorization", "Bearer " + userToken));
        mockMvc.perform(post("/prestamos/usr-001/lib-002").header("Authorization", "Bearer " + userToken));
        mockMvc.perform(post("/prestamos/usr-001/lib-003").header("Authorization", "Bearer " + userToken));
        mockMvc.perform(post("/prestamos/usr-001/lib-004").header("Authorization", "Bearer " + userToken))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void devolverLibroQueNoExisteRetorna404() throws Exception {
        mockMvc.perform(put("/prestamos/devolver/usr-001/no-existe")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void devolverLibroSinPrestamoActivoRetorna404() throws Exception {
        mockMvc.perform(put("/prestamos/devolver/usr-001/lib-001")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void prestarConIdUsuarioInvalidoRetorna400() throws Exception {
        mockMvc.perform(post("/prestamos/ /lib-001")
                .header("Authorization", "Bearer " + librarianToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void devolverConIdUsuarioInvalidoRetorna400() throws Exception {
        mockMvc.perform(put("/prestamos/devolver/ /lib-001")
                .header("Authorization", "Bearer " + librarianToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void prestarLibroQueNoExisteRetorna404() throws Exception {
        mockMvc.perform(post("/prestamos/usr-001/no-existe")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }
}
