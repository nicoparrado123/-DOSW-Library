package edu.eci.dosw.tdd;

import edu.eci.dosw.tdd.core.exception.UsuarioNoEncontradoException;
import edu.eci.dosw.tdd.core.model.Usuario;
import edu.eci.dosw.tdd.core.service.ServicioUsuarios;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServicioUsuariosTest {

    private ServicioUsuarios servicioUsuarios;
    private Usuario nico;

    @BeforeEach
    void iniciar() {
        servicioUsuarios = new ServicioUsuarios();
        nico = new Usuario("nico-001", "nico");
        servicioUsuarios.registrar(nico);
    }

    @Test
    void obtenerTodosLosUsuarios() {
        assertEquals(1, servicioUsuarios.obtenerTodos().size());
    }

    @Test
    void buscarUsuarioPorId() throws UsuarioNoEncontradoException {
        assertEquals(nico, servicioUsuarios.buscarPorId("nico-001"));
    }

    @Test
    void buscarUsuarioIdInexistente() {
        assertThrows(UsuarioNoEncontradoException.class, () -> servicioUsuarios.buscarPorId("nico-999"));
    }

    @Test
    void registrarVariosUsuarios() {
        servicioUsuarios.registrar(new Usuario("nico-002", "amigo de nico"));
        assertEquals(2, servicioUsuarios.obtenerTodos().size());
    }
}
