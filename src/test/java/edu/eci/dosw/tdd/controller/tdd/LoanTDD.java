package edu.eci.dosw.tdd.controller.tdd;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.service.BookService;
import edu.eci.dosw.tdd.core.service.LoanService;
import edu.eci.dosw.tdd.core.service.UserService;
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

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Autowired
    private LoanService loanService;

    @BeforeEach
    void preparar() {
        bookService.limpiar();
        userService.limpiar();
        loanService.limpiar();
        userService.registrar(new User("usr-001", "Nico"));
        bookService.agregarLibro(new Book("lib-001", "Clean Code", "Martin"), 2);
        bookService.agregarLibro(new Book("lib-002", "Refactoring", "Fowler"), 1);
    }

    @Test
    void prestarLibroExitoso() throws Exception {
        mockMvc.perform(post("/prestamos/usr-001/lib-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    @Test
    void devolverLibroExitoso() throws Exception {
        mockMvc.perform(post("/prestamos/usr-001/lib-001"));

        mockMvc.perform(put("/prestamos/devolver/usr-001/lib-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("DEVUELTO"));
    }

    @Test
    void prestarLibroSinEjemplaresRetorna409() throws Exception {
        mockMvc.perform(post("/prestamos/usr-001/lib-002"));
        mockMvc.perform(post("/prestamos/usr-001/lib-002"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void prestarUsuarioInexistenteRetorna404() throws Exception {
        mockMvc.perform(post("/prestamos/no-existe/lib-001"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void obtenerTodosLosPrestamos() throws Exception {
        mockMvc.perform(post("/prestamos/usr-001/lib-001"));

        mockMvc.perform(get("/prestamos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
