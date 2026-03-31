package edu.eci.dosw.tdd.persistence.relational.mapper;

import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.persistence.relational.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserEntityMapper {
    public User toDomain(UserEntity e) {
        User u = new User(e.getId(), e.getNombre());
        u.setUsername(e.getUsername());
        u.setPassword(e.getPassword());
        u.setRole(e.getRole());
        return u;
    }
    public UserEntity toEntity(User u, String encodedPassword) {
        return new UserEntity(u.getId(), u.getNombre(), u.getUsername(), encodedPassword,
                u.getRole() != null ? u.getRole() : UserEntity.Role.USER);
    }
}
