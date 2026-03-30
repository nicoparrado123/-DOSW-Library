package edu.eci.dosw.tdd.controller.tdd;

import edu.eci.dosw.tdd.persistence.relational.entity.UserEntity;
import edu.eci.dosw.tdd.persistence.relational.repository.LoanRepository;
import edu.eci.dosw.tdd.persistence.relational.repository.UserRepository;
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
public class UserTDD {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private LoanRepository loanRepository;
    @Autowired private JwtService jwtService;

    private String librarianToken;

    @BeforeEach
    void limpiar() {
        loanRepository.deleteAll();
        userRepository.deleteAll();
        librarianToken = jwtService.generateToken("lib-001", "LIBRARIAN");
    }

    @Test
    void registrarUsuarioYObtenerTodos() throws Exception {
        mockMvc.perform(post("/usuarios")
                .header("Authorization", "Bearer " + librarianToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"usr-001\",\"nombre\":\"Nico\",\"username\":\"nico\",\"password\":\"pass12\",\"role\":\"USER\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("usr-001"));

        mockMvc.perform(get("/usuarios")
                .header("Authorization", "Bearer " + librarianToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void obtenerUsuarioPorId() throws Exception {
        mockMvc.perform(post("/usuarios")
                .header("Authorization", "Bearer " + librarianToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"usr-002\",\"nombre\":\"Ana\",\"username\":\"ana\",\"password\":\"pass12\",\"role\":\"USER\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/usuarios/usr-002")
                .header("Authorization", "Bearer " + librarianToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ana"));
    }

    @Test
    void obtenerUsuarioInexistenteRetorna404() throws Exception {
        mockMvc.perform(get("/usuarios/no-existe")
                .header("Authorization", "Bearer " + librarianToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void registrarUsuarioConNombreVacioRetorna400() throws Exception {
        mockMvc.perform(post("/usuarios")
                .header("Authorization", "Bearer " + librarianToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"usr-003\",\"nombre\":\"\",\"username\":\"u3\",\"password\":\"pass\",\"role\":\"USER\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}
