package edu.eci.dosw.tdd;

import edu.eci.dosw.tdd.core.exception.LibroNoDisponibleException;
import edu.eci.dosw.tdd.core.exception.LibroNoEncontradoException;
import edu.eci.dosw.tdd.core.exception.LimitePrestamosException;
import edu.eci.dosw.tdd.core.exception.UsuarioNoEncontradoException;
import edu.eci.dosw.tdd.core.model.EstadoPrestamo;
import edu.eci.dosw.tdd.core.model.Libro;
import edu.eci.dosw.tdd.core.model.Usuario;
import edu.eci.dosw.tdd.core.service.ServicioLibros;
import edu.eci.dosw.tdd.core.service.ServicioPrestamos;
import edu.eci.dosw.tdd.core.service.ServicioUsuarios;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServicioPrestamosTest {

    private ServicioPrestamos servicioPrestamos;
    private ServicioLibros servicioLibros;
    private ServicioUsuarios servicioUsuarios;

    @BeforeEach
    void iniciar() {
        servicioLibros = new ServicioLibros();
        servicioUsuarios = new ServicioUsuarios();
        servicioPrestamos = new ServicioPrestamos(servicioLibros, servicioUsuarios);

        servicioUsuarios.registrar(new Usuario("nico-001", "nico"));
        servicioLibros.agregarLibro(new Libro("nico-l1", "clean code", "martin"), 2);
        servicioLibros.agregarLibro(new Libro("nico-l2", "refactoring", "fowler"), 1);
        servicioLibros.agregarLibro(new Libro("nico-l3", "ddd", "evans"), 1);
        servicioLibros.agregarLibro(new Libro("nico-l4", "sicp", "abelson"), 1);
    }

    @Test
    void prestarLibroExitoso() throws Exception {
        var prestamo = servicioPrestamos.prestar("nico-001", "nico-l1");
        assertEquals(EstadoPrestamo.ACTIVO, prestamo.getEstado());
    }

    @Test
    void prestarLibroReduceEjemplares() throws Exception {
        servicioPrestamos.prestar("nico-001", "nico-l1");
        assertEquals(1, servicioLibros.obtenerEjemplares("nico-l1"));
    }

    @Test
    void devolverLibroExitoso() throws Exception {
        servicioPrestamos.prestar("nico-001", "nico-l1");
        var prestamo = servicioPrestamos.devolver("nico-001", "nico-l1");
        assertEquals(EstadoPrestamo.DEVUELTO, prestamo.getEstado());
        assertNotNull(prestamo.getFechaDevolucion());
    }

    @Test
    void devolverLibroRestaureaEjemplares() throws Exception {
        servicioPrestamos.prestar("nico-001", "nico-l1");
        servicioPrestamos.devolver("nico-001", "nico-l1");
        assertEquals(2, servicioLibros.obtenerEjemplares("nico-l1"));
    }

    @Test
    void prestarSinEjemplaresLanzaExcepcion() throws Exception {
        servicioPrestamos.prestar("nico-001", "nico-l2");
        assertThrows(LibroNoDisponibleException.class, () -> servicioPrestamos.prestar("nico-001", "nico-l2"));
    }

    @Test
    void prestarSuperaLimiteLanzaExcepcion() throws Exception {
        servicioPrestamos.prestar("nico-001", "nico-l1");
        servicioPrestamos.prestar("nico-001", "nico-l2");
        servicioPrestamos.prestar("nico-001", "nico-l3");
        assertThrows(LimitePrestamosException.class, () -> servicioPrestamos.prestar("nico-001", "nico-l4"));
    }

    @Test
    void prestarUsuarioInexistenteLanzaExcepcion() {
        assertThrows(UsuarioNoEncontradoException.class, () -> servicioPrestamos.prestar("nico-999", "nico-l1"));
    }

    @Test
    void prestarLibroInexistenteLanzaExcepcion() {
        assertThrows(LibroNoEncontradoException.class, () -> servicioPrestamos.prestar("nico-001", "nico-999"));
    }

    @Test
    void obtenerPrestamosPorUsuario() throws Exception {
        servicioPrestamos.prestar("nico-001", "nico-l1");
        assertEquals(1, servicioPrestamos.obtenerPorUsuario("nico-001").size());
    }

    @Test
    void obtenerTodosLosPrestamos() throws Exception {
        servicioPrestamos.prestar("nico-001", "nico-l1");
        assertEquals(1, servicioPrestamos.obtenerTodos().size());
    }
}
