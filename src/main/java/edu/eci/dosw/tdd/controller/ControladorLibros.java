package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.core.exception.LibroNoEncontradoException;
import edu.eci.dosw.tdd.core.model.Libro;
import edu.eci.dosw.tdd.core.service.ServicioLibros;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/libros")
public class ControladorLibros {

    private final ServicioLibros servicioLibros;

    public ControladorLibros(ServicioLibros servicioLibros) {
        this.servicioLibros = servicioLibros;
    }

    @GetMapping
    public List<Libro> obtenerTodos() {
        return servicioLibros.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Libro> obtenerPorId(@PathVariable String id) {
        try {
            return ResponseEntity.ok(servicioLibros.buscarPorId(id));
        } catch (LibroNoEncontradoException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Libro> agregar(@RequestBody Libro libro, @RequestParam int ejemplares) {
        servicioLibros.agregarLibro(libro, ejemplares);
        return ResponseEntity.ok(libro);
    }
}
