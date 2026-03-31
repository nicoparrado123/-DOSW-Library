package edu.eci.dosw.tdd.core.repository;

import edu.eci.dosw.tdd.core.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
    void save(User user);
    Optional<User> findById(String id);
    Optional<User> findByUsername(String username);
    List<User> findAll();
}
