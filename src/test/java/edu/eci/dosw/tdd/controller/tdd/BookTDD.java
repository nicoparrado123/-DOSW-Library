package edu.eci.dosw.tdd.controller.tdd;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.service.BookService;
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

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookService bookService;

    @BeforeEach
    void limpiar() {
        bookService.limpiar();
    }

    @Test
    void agregarLibroYObtenerTodos() throws Exception {
        mockMvc.perform(post("/libros")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"lib-001\",\"titulo\":\"Clean Code\",\"autor\":\"Martin\",\"copies\":3}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("lib-001"));

        mockMvc.perform(get("/libros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void obtenerLibroPorId() throws Exception {
        bookService.agregarLibro(new Book("lib-002", "Refactoring", "Fowler"), 2);

        mockMvc.perform(get("/libros/lib-002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Refactoring"));
    }

    @Test
    void obtenerLibroInexistenteRetorna404() throws Exception {
        mockMvc.perform(get("/libros/no-existe"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void agregarLibroConIdInvalidoRetorna400() throws Exception {
        mockMvc.perform(post("/libros")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"\",\"titulo\":\"Titulo\",\"autor\":\"Autor\",\"copies\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}
