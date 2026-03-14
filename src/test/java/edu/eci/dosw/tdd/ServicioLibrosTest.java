package edu.eci.dosw.tdd;

import edu.eci.dosw.tdd.core.exception.LibroNoEncontradoException;
import edu.eci.dosw.tdd.core.model.Libro;
import edu.eci.dosw.tdd.core.service.ServicioLibros;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServicioLibrosTest {

    private ServicioLibros servicioLibros;
    private Libro libroNico;

    @BeforeEach
    void iniciar() {
        servicioLibros = new ServicioLibros();
        libroNico = new Libro("nico-001", "clean code", "martin");
        servicioLibros.agregarLibro(libroNico, 3);
    }

    @Test
    void obtenerTodosLosLibros() {
        assertEquals(1, servicioLibros.obtenerTodos().size());
    }

    @Test
    void buscarLibroPorId() throws LibroNoEncontradoException {
        assertEquals(libroNico, servicioLibros.buscarPorId("nico-001"));
    }

    @Test
    void buscarLibroIdInexistente() {
        assertThrows(LibroNoEncontradoException.class, () -> servicioLibros.buscarPorId("nico-999"));
    }

    @Test
    void obtenerEjemplares() throws LibroNoEncontradoException {
        assertEquals(3, servicioLibros.obtenerEjemplares("nico-001"));
    }

    @Test
    void actualizarEjemplares() throws LibroNoEncontradoException {
        servicioLibros.actualizarEjemplares("nico-001", 5);
        assertEquals(5, servicioLibros.obtenerEjemplares("nico-001"));
    }

    @Test
    void agregarLibroAcumulaEjemplares() throws LibroNoEncontradoException {
        servicioLibros.agregarLibro(libroNico, 2);
        assertEquals(5, servicioLibros.obtenerEjemplares("nico-001"));
    }
}
