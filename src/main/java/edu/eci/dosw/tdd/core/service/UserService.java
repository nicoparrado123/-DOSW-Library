package edu.eci.dosw.tdd.core.service;

import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.validator.UserValidator;
import edu.eci.dosw.tdd.persistence.entity.UserEntity;
import edu.eci.dosw.tdd.persistence.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserValidator validator;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserValidator validator, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.validator = validator;
        this.passwordEncoder = passwordEncoder;
    }

    public void registrar(User usuario) {
        validator.validar(usuario.getId(), usuario.getNombre());
        UserEntity entity = new UserEntity(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getUsername(),
                passwordEncoder.encode(usuario.getPassword()),
                usuario.getRole() != null ? usuario.getRole() : UserEntity.Role.USER
        );
        userRepository.save(entity);
    }

    public List<User> obtenerTodos() {
        return userRepository.findAll().stream()
                .map(e -> new User(e.getId(), e.getNombre()))
                .toList();
    }

    public User buscarPorId(String id) throws UserNotFoundException {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("no existe el usuario con id: " + id));
        return new User(entity.getId(), entity.getNombre());
    }

    public UserEntity buscarEntidadPorId(String id) throws UserNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("no existe el usuario con id: " + id));
    }
}
