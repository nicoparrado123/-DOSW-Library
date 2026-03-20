package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.validator.UserValidator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final List<User> usuarios = new ArrayList<>();
    private final UserValidator validator;

    public UserService(UserValidator validator) {
        this.validator = validator;
    }

    public void registrar(User usuario) {
        validator.validar(usuario.getId(), usuario.getNombre());
        usuarios.add(usuario);
    }

    public List<User> obtenerTodos() {
        return new ArrayList<>(usuarios);
    }

    public User buscarPorId(String id) throws UserNotFoundException {
        for (User usuario : usuarios) {
            if (usuario.getId().equals(id)) {
                return usuario;
            }
        }
        throw new UserNotFoundException("no existe el usuario con id: " + id);
    }

    public void limpiar() {
        usuarios.clear();
    }
}
