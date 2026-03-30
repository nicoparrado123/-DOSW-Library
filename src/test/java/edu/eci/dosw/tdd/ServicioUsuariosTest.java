package edu.eci.dosw.tdd;

import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.service.UserService;
import edu.eci.dosw.tdd.core.validator.UserValidator;
import edu.eci.dosw.tdd.persistence.entity.UserEntity;
import edu.eci.dosw.tdd.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ServicioUsuariosTest {

    private UserService userService;
    private UserRepository userRepository;
    private UserEntity entityNico;

    @BeforeEach
    void iniciar() {
        userRepository = mock(UserRepository.class);
        PasswordEncoder encoder = mock(PasswordEncoder.class);
        when(encoder.encode(any())).thenReturn("hashed");
        userService = new UserService(userRepository, new UserValidator(), encoder);
        entityNico = new UserEntity("nico-001", "nico", "nico_user", "hashed", UserEntity.Role.USER);
        when(userRepository.findById("nico-001")).thenReturn(Optional.of(entityNico));
        when(userRepository.findAll()).thenReturn(List.of(entityNico));
    }

    @Test
    void obtenerTodosLosUsuarios() {
        assertEquals(1, userService.obtenerTodos().size());
    }

    @Test
    void buscarUsuarioPorId() throws UserNotFoundException {
        assertEquals("nico-001", userService.buscarPorId("nico-001").getId());
    }

    @Test
    void buscarUsuarioIdInexistente() {
        when(userRepository.findById("nico-999")).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.buscarPorId("nico-999"));
    }

    @Test
    void registrarUsuario() {
        User user = new User("nico-002", "amigo de nico");
        user.setUsername("amigo");
        user.setPassword("pass12");
        userService.registrar(user);
        verify(userRepository).save(any(UserEntity.class));
    }
}
