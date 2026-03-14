package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.LibroNoEncontradoException;
import edu.eci.dosw.tdd.core.model.Libro;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ServicioLibros {

    private final Map<Libro, Integer> libros = new HashMap<>();

    public void agregarLibro(Libro libro, int ejemplares) {
        libros.merge(libro, ejemplares, Integer::sum);
    }

    public List<Libro> obtenerTodos() {
        return new ArrayList<>(libros.keySet());
    }

    public Libro buscarPorId(String id) throws LibroNoEncontradoException {
        return libros.keySet().stream()
                .filter(l -> l.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new LibroNoEncontradoException("no existe el libro con id: " + id));
    }

    public int obtenerEjemplares(String id) throws LibroNoEncontradoException {
        return libros.get(buscarPorId(id));
    }

    public void actualizarEjemplares(String id, int cantidad) throws LibroNoEncontradoException {
        libros.put(buscarPorId(id), cantidad);
    }
}
