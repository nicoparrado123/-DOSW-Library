package edu.eci.dosw.tdd.core.model;

import edu.eci.dosw.tdd.persistence.relational.entity.UserEntity;

public class User {
    private String id;
    private String nombre;
    private String username;
    private String password;
    private UserEntity.Role role;

    public User() {}

    public User(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public UserEntity.Role getRole() { return role; }
    public void setId(String id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(UserEntity.Role role) { this.role = role; }
}
