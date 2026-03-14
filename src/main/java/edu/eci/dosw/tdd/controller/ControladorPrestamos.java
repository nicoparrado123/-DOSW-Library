package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.core.model.Prestamo;
import edu.eci.dosw.tdd.core.service.ServicioPrestamos;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/prestamos")
public class ControladorPrestamos {

    private final ServicioPrestamos servicioPrestamos;

    public ControladorPrestamos(ServicioPrestamos servicioPrestamos) {
        this.servicioPrestamos = servicioPrestamos;
    }

    @GetMapping
    public List<Prestamo> obtenerTodos() {
        return servicioPrestamos.obtenerTodos();
    }

    @PostMapping("/{idUsuario}/{idLibro}")
    public ResponseEntity<?> prestar(@PathVariable String idUsuario, @PathVariable String idLibro) {
        try {
            return ResponseEntity.ok(servicioPrestamos.prestar(idUsuario, idLibro));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/devolver/{idUsuario}/{idLibro}")
    public ResponseEntity<?> devolver(@PathVariable String idUsuario, @PathVariable String idLibro) {
        try {
            return ResponseEntity.ok(servicioPrestamos.devolver(idUsuario, idLibro));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
