package edu.eci.dosw.tdd.persistence.nonrelational.impl;

import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.repository.UserRepositoryPort;
import edu.eci.dosw.tdd.persistence.nonrelational.mapper.UserDocumentMapper;
import edu.eci.dosw.tdd.persistence.nonrelational.repository.UserMongoRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("mongo")
public class UserRepositoryMongoImpl implements UserRepositoryPort {

    private final UserMongoRepository repository;
    private final UserDocumentMapper mapper;
    private final PasswordEncoder passwordEncoder;

    public UserRepositoryMongoImpl(UserMongoRepository repository, UserDocumentMapper mapper, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void save(User user) {
        repository.save(mapper.toDocument(user, passwordEncoder.encode(user.getPassword())));
    }

    @Override
    public Optional<User> findById(String id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username).map(mapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return repository.findAll().stream().map(mapper::toDomain).toList();
    }
}
