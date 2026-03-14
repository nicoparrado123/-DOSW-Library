package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.UsuarioNoEncontradoException;
import edu.eci.dosw.tdd.core.model.Usuario;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServicioUsuarios {

    private final List<Usuario> usuarios = new ArrayList<>();

    public void registrar(Usuario usuario) {
        usuarios.add(usuario);
    }

    public List<Usuario> obtenerTodos() {
        return new ArrayList<>(usuarios);
    }

    public Usuario buscarPorId(String id) throws UsuarioNoEncontradoException {
        return usuarios.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new UsuarioNoEncontradoException("no existe el usuario con id: " + id));
    }
}
