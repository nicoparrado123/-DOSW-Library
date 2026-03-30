package edu.eci.dosw.tdd.controller.tdd;

import edu.eci.dosw.tdd.persistence.relational.entity.BookEntity;
import edu.eci.dosw.tdd.persistence.relational.repository.BookRepository;
import edu.eci.dosw.tdd.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BookTDD {

    @Autowired private MockMvc mockMvc;
    @Autowired private BookRepository bookRepository;
    @Autowired private JwtService jwtService;

    private String librarianToken;
    private String userToken;

    @BeforeEach
    void limpiar() {
        bookRepository.deleteAll();
        librarianToken = jwtService.generateToken("lib-001", "LIBRARIAN");
        userToken = jwtService.generateToken("usr-001", "USER");
    }

    @Test
    void agregarLibroYObtenerTodos() throws Exception {
        mockMvc.perform(post("/libros")
                .header("Authorization", "Bearer " + librarianToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"lib-001\",\"titulo\":\"Clean Code\",\"autor\":\"Martin\",\"copies\":3}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("lib-001"));

        mockMvc.perform(get("/libros")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void obtenerLibroPorId() throws Exception {
        bookRepository.save(new BookEntity("lib-002", "Refactoring", "Fowler", 2, 2));

        mockMvc.perform(get("/libros/lib-002")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Refactoring"));
    }

    @Test
    void obtenerLibroInexistenteRetorna404() throws Exception {
        mockMvc.perform(get("/libros/no-existe")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void agregarLibroConIdInvalidoRetorna400() throws Exception {
        mockMvc.perform(post("/libros")
                .header("Authorization", "Bearer " + librarianToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"\",\"titulo\":\"Titulo\",\"autor\":\"Autor\",\"copies\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void sinTokenRetorna401() throws Exception {
        mockMvc.perform(get("/libros"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void userNoPuedeAgregarLibro() throws Exception {
        mockMvc.perform(post("/libros")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"lib-003\",\"titulo\":\"Test\",\"autor\":\"Autor\",\"copies\":1}"))
                .andExpect(status().isForbidden());
    }
}
