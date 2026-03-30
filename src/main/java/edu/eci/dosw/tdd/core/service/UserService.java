package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.repository.UserRepositoryPort;
import edu.eci.dosw.tdd.core.validator.UserValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepositoryPort userRepository;
    private final UserValidator validator;

    public UserService(UserRepositoryPort userRepository, UserValidator validator) {
        this.userRepository = userRepository;
        this.validator = validator;
    }

    public void registrar(User usuario) {
        validator.validar(usuario.getId(), usuario.getNombre());
        validator.validarCredenciales(usuario.getUsername(), usuario.getPassword());
        userRepository.save(usuario);
    }

    public List<User> obtenerTodos() {
        return userRepository.findAll();
    }

    public User buscarPorId(String id) throws UserNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("no existe el usuario con id: " + id));
    }

    public User buscarEntidadPorId(String id) throws UserNotFoundException {
        return buscarPorId(id);
    }
}
