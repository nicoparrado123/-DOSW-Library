package edu.eci.dosw.tdd.persistence.nonrelational.mapper;

import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.persistence.nonrelational.document.UserDocument;
import edu.eci.dosw.tdd.persistence.relational.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserDocumentMapper {
    public User toDomain(UserDocument d) {
        User u = new User(d.getId(), d.getNombre());
        u.setUsername(d.getUsername());
        u.setPassword(d.getPassword());
        if (d.getRole() != null) u.setRole(UserEntity.Role.valueOf(d.getRole()));
        return u;
    }
    public UserDocument toDocument(User u, String encodedPassword) {
        UserDocument doc = new UserDocument();
        doc.setId(u.getId());
        doc.setNombre(u.getNombre());
        doc.setUsername(u.getUsername());
        doc.setPassword(encodedPassword);
        doc.setRole(u.getRole() != null ? u.getRole().name() : "USER");
        return doc;
    }
}
