package edu.eci.dosw.tdd.core.model;

public class Book {
    private String id;
    private String titulo;
    private String autor;
    private int stockDisponible;

    public Book() {}

    public Book(String id, String titulo, String autor, int stockDisponible) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.stockDisponible = stockDisponible;
    }

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public int getStockDisponible() { return stockDisponible; }
    public void setId(String id) { this.id = id; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setAutor(String autor) { this.autor = autor; }
    public void setStockDisponible(int stockDisponible) { this.stockDisponible = stockDisponible; }
}
