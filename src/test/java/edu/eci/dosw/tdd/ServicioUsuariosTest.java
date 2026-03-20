package edu.eci.dosw.tdd;

import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.service.UserService;
import edu.eci.dosw.tdd.core.validator.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServicioUsuariosTest {

    private UserService userService;
    private User nico;

    @BeforeEach
    void iniciar() {
        userService = new UserService(new UserValidator());
        nico = new User("nico-001", "nico");
        userService.registrar(nico);
    }

    @Test
    void obtenerTodosLosUsuarios() {
        assertEquals(1, userService.obtenerTodos().size());
    }

    @Test
    void buscarUsuarioPorId() throws UserNotFoundException {
        assertEquals(nico, userService.buscarPorId("nico-001"));
    }

    @Test
    void buscarUsuarioIdInexistente() {
        assertThrows(UserNotFoundException.class, () -> userService.buscarPorId("nico-999"));
    }

    @Test
    void registrarVariosUsuarios() {
        userService.registrar(new User("nico-002", "amigo de nico"));
        assertEquals(2, userService.obtenerTodos().size());
    }
}
