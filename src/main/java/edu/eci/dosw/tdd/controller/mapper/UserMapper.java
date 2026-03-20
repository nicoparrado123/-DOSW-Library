package edu.eci.dosw.tdd.controller.mapper;

import edu.eci.dosw.tdd.core.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toModel(UserDTO dto) {
        return new User(dto.getId(), dto.getNombre());
    }

    public UserDTO toDTO(User usuario) {
        UserDTO dto = new UserDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        return dto;
    }
}
