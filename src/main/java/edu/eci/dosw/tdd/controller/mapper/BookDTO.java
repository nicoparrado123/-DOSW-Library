package edu.eci.dosw.tdd.controller.mapper;

public class BookDTO {
    private String id;
    private String titulo;
    private String autor;
    private int copies;

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public int getCopies() { return copies; }
    public void setId(String id) { this.id = id; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setAutor(String autor) { this.autor = autor; }
    public void setCopies(int copies) { this.copies = copies; }
}
