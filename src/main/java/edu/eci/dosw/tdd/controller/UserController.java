package edu.eci.dosw.tdd.controller;

import edu.eci.dosw.tdd.controller.mapper.UserDTO;
import edu.eci.dosw.tdd.controller.mapper.UserMapper;
import edu.eci.dosw.tdd.core.exception.UserNotFoundException;
import edu.eci.dosw.tdd.core.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public List<UserDTO> obtenerTodos() {
        return userService.obtenerTodos().stream().map(userMapper::toDTO).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public UserDTO obtenerPorId(@PathVariable String id) throws UserNotFoundException {
        return userMapper.toDTO(userService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<UserDTO> registrar(@RequestBody UserDTO dto) {
        userService.registrar(userMapper.toModel(dto));
        return ResponseEntity.ok(dto);
    }
}
