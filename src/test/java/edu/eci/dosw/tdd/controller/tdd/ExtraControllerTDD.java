package edu.eci.dosw.tdd.controller.tdd;

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

import edu.eci.dosw.tdd.persistence.nonrelational.document.BookDocument;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("mongo")
public class ExtraControllerTDD {

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

        for (String[] b : new String[][]{
            {"lib-001","Clean Code","Martin","3"},
            {"lib-002","Refactoring","Fowler","1"},
            {"lib-003","DDD","Evans","1"},
            {"lib-004","SICP","Abelson","1"}
        }) {
            bookMongoRepository.save(buildBook(b[0], b[1], b[2], Integer.parseInt(b[3])));
        }

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
