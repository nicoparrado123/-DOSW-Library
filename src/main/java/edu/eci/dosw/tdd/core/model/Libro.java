package edu.eci.dosw.tdd.core.model;

public class Libro {
    private String id;
    private String titulo;
    private String autor;

    public Libro(String id, String titulo, String autor) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
    }

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
}
