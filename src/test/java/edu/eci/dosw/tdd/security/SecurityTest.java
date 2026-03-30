package edu.eci.dosw.tdd.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.context.ActiveProfiles;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("mongo")
class SecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Test
    void sinTokenRetorna401() throws Exception {
        mockMvc.perform(get("/libros"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenInvalidoRetorna401() throws Exception {
        mockMvc.perform(get("/libros")
                .header("Authorization", "Bearer token.invalido.aqui"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rolIncorrectoRetorna403() throws Exception {
        String token = jwtService.generateToken("usr-001", "USER");
        mockMvc.perform(post("/libros")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"lib-001\",\"titulo\":\"Test\",\"autor\":\"Autor\",\"copies\":1}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void librarianPuedeAgregarLibro() throws Exception {
        String token = jwtService.generateToken("lib-001", "LIBRARIAN");
        mockMvc.perform(post("/libros")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"sec-lib-001\",\"titulo\":\"Security Book\",\"autor\":\"Autor\",\"copies\":2}"))
                .andExpect(status().isOk());
    }

    @Test
    void userPuedeVerLibros() throws Exception {
        String token = jwtService.generateToken("usr-001", "USER");
        mockMvc.perform(get("/libros")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void userNoPuedeVerTodosLosPrestamos() throws Exception {
        String token = jwtService.generateToken("usr-001", "USER");
        mockMvc.perform(get("/prestamos")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void librarianPuedeVerTodosLosPrestamos() throws Exception {
        String token = jwtService.generateToken("lib-001", "LIBRARIAN");
        mockMvc.perform(get("/prestamos")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void userNoPuedeVerUsuariosAjenos() throws Exception {
        String token = jwtService.generateToken("usr-001", "USER");
        mockMvc.perform(get("/usuarios")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}
