package edu.eci.dosw.tdd.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    private String id;
    private String nombre;
    private String username;
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role { USER, LIBRARIAN }

    public UserEntity() {}

    public UserEntity(String id, String nombre, String username, String password, Role role) {
        this.id = id;
        this.nombre = nombre;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public void setId(String id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setRole(Role role) { this.role = role; }
}
