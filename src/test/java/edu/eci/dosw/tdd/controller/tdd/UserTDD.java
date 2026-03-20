package edu.eci.dosw.tdd.controller.tdd;

import edu.eci.dosw.tdd.core.service.UserService;
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

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @BeforeEach
    void limpiar() {
        userService.limpiar();
    }

    @Test
    void registrarUsuarioYObtenerTodos() throws Exception {
        mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"usr-001\",\"nombre\":\"Nico\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("usr-001"));

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void obtenerUsuarioPorId() throws Exception {
        mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"usr-002\",\"nombre\":\"Ana\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/usuarios/usr-002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ana"));
    }

    @Test
    void obtenerUsuarioInexistenteRetorna404() throws Exception {
        mockMvc.perform(get("/usuarios/no-existe"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void registrarUsuarioConNombreVacioRetorna400() throws Exception {
        mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"usr-003\",\"nombre\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}
