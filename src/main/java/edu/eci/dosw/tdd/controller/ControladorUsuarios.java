package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.core.exception.UsuarioNoEncontradoException;
import edu.eci.dosw.tdd.core.model.Usuario;
import edu.eci.dosw.tdd.core.service.ServicioUsuarios;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class ControladorUsuarios {

    private final ServicioUsuarios servicioUsuarios;

    public ControladorUsuarios(ServicioUsuarios servicioUsuarios) {
        this.servicioUsuarios = servicioUsuarios;
    }

    @GetMapping
    public List<Usuario> obtenerTodos() {
        return servicioUsuarios.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable String id) {
        try {
            return ResponseEntity.ok(servicioUsuarios.buscarPorId(id));
        } catch (UsuarioNoEncontradoException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Usuario> registrar(@RequestBody Usuario usuario) {
        servicioUsuarios.registrar(usuario);
        return ResponseEntity.ok(usuario);
    }
}
