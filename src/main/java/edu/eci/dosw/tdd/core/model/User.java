package edu.eci.dosw.tdd.core.model;

public class User {
    private String id;
    private String nombre;

    public User() {}

    public User(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public void setId(String id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
