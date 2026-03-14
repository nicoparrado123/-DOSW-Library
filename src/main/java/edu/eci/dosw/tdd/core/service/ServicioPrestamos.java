package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.LibroNoDisponibleException;
import edu.eci.dosw.tdd.core.exception.LibroNoEncontradoException;
import edu.eci.dosw.tdd.core.exception.LimitePrestamosException;
import edu.eci.dosw.tdd.core.exception.UsuarioNoEncontradoException;
import edu.eci.dosw.tdd.core.model.EstadoPrestamo;
import edu.eci.dosw.tdd.core.model.Prestamo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ServicioPrestamos {

    private static final int limite = 3;

    private final List<Prestamo> prestamos = new ArrayList<>();
    private final ServicioLibros servicioLibros;
    private final ServicioUsuarios servicioUsuarios;

    public ServicioPrestamos(ServicioLibros servicioLibros, ServicioUsuarios servicioUsuarios) {
        this.servicioLibros = servicioLibros;
        this.servicioUsuarios = servicioUsuarios;
    }

    public Prestamo prestar(String idUsuario, String idLibro)
            throws UsuarioNoEncontradoException, LibroNoEncontradoException, LibroNoDisponibleException, LimitePrestamosException {

        var usuario = servicioUsuarios.buscarPorId(idUsuario);
        var libro = servicioLibros.buscarPorId(idLibro);

        if (servicioLibros.obtenerEjemplares(idLibro) <= 0)
            throw new LibroNoDisponibleException("no hay ejemplares del libro: " + idLibro);

        long activos = prestamos.stream()
                .filter(p -> p.getUsuario().getId().equals(idUsuario) && p.getEstado() == EstadoPrestamo.ACTIVO)
                .count();
        if (activos >= limite)
            throw new LimitePrestamosException("el usuario ya tiene " + limite + " prestamos activos: " + idUsuario);

        servicioLibros.actualizarEjemplares(idLibro, servicioLibros.obtenerEjemplares(idLibro) - 1);
        Prestamo prestamo = new Prestamo(libro, usuario, LocalDate.now());
        prestamos.add(prestamo);
        return prestamo;
    }

    public Prestamo devolver(String idUsuario, String idLibro)
            throws UsuarioNoEncontradoException, LibroNoEncontradoException {

        servicioUsuarios.buscarPorId(idUsuario);
        servicioLibros.buscarPorId(idLibro);

        Prestamo prestamo = prestamos.stream()
                .filter(p -> p.getUsuario().getId().equals(idUsuario)
                        && p.getLibro().getId().equals(idLibro)
                        && p.getEstado() == EstadoPrestamo.ACTIVO)
                .findFirst()
                .orElseThrow(() -> new LibroNoEncontradoException("no hay prestamo activo del libro: " + idLibro));

        prestamo.setEstado(EstadoPrestamo.DEVUELTO);
        prestamo.setFechaDevolucion(LocalDate.now());
        servicioLibros.actualizarEjemplares(idLibro, servicioLibros.obtenerEjemplares(idLibro) + 1);
        return prestamo;
    }

    public List<Prestamo> obtenerTodos() {
        return new ArrayList<>(prestamos);
    }

    public List<Prestamo> obtenerPorUsuario(String idUsuario) throws UsuarioNoEncontradoException {
        servicioUsuarios.buscarPorId(idUsuario);
        return prestamos.stream()
                .filter(p -> p.getUsuario().getId().equals(idUsuario))
                .toList();
    }
}
