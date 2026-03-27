package edu.eci.dosw.tdd.controller.mapper;

import edu.eci.dosw.tdd.core.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toModel(UserDTO dto) {
        User user = new User(dto.getId(), dto.getNombre());
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        return user;
    }

    public UserDTO toDTO(User usuario) {
        UserDTO dto = new UserDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        return dto;
    }
}
