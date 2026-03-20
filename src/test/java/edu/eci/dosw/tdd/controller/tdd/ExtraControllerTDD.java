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
public class ExtraControllerTDD {

    @Autowired private MockMvc mockMvc;
    @Autowired private BookService bookService;
    @Autowired private UserService userService;
    @Autowired private LoanService loanService;

    @BeforeEach
    void preparar() {
        bookService.limpiar();
        userService.limpiar();
        loanService.limpiar();
        userService.registrar(new User("usr-001", "Nico"));
        bookService.agregarLibro(new Book("lib-001", "Clean Code", "Martin"), 3);
        bookService.agregarLibro(new Book("lib-002", "Refactoring", "Fowler"), 1);
        bookService.agregarLibro(new Book("lib-003", "DDD", "Evans"), 1);
        bookService.agregarLibro(new Book("lib-004", "SICP", "Abelson"), 1);
    }

    @Test
    void prestarCuandoYaTiene3ActivosRetorna409() throws Exception {
        mockMvc.perform(post("/prestamos/usr-001/lib-001"));
        mockMvc.perform(post("/prestamos/usr-001/lib-002"));
        mockMvc.perform(post("/prestamos/usr-001/lib-003"));
        mockMvc.perform(post("/prestamos/usr-001/lib-004"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void devolverLibroQueNoExisteRetorna404() throws Exception {
        mockMvc.perform(put("/prestamos/devolver/usr-001/no-existe"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void devolverLibroSinPrestamoActivoRetorna404() throws Exception {
        mockMvc.perform(put("/prestamos/devolver/usr-001/lib-001"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void prestarConIdUsuarioInvalidoRetorna400() throws Exception {
        mockMvc.perform(post("/prestamos/ /lib-001"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void devolverConIdUsuarioInvalidoRetorna400() throws Exception {
        mockMvc.perform(put("/prestamos/devolver/ /lib-001"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void prestarLibroQueNoExisteRetorna404() throws Exception {
        mockMvc.perform(post("/prestamos/usr-001/no-existe"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }
}
