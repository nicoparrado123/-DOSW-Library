package edu.eci.dosw.tdd.persistence.relational.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class BookEntity {

    @Id
    private String id;
    private String titulo;
    private String autor;
    private int stockTotal;
    private int stockDisponible;

    public BookEntity() {}

    public BookEntity(String id, String titulo, String autor, int stockTotal, int stockDisponible) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.stockTotal = stockTotal;
        this.stockDisponible = stockDisponible;
    }

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public int getStockTotal() { return stockTotal; }
    public int getStockDisponible() { return stockDisponible; }
    public void setId(String id) { this.id = id; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setAutor(String autor) { this.autor = autor; }
    public void setStockTotal(int stockTotal) { this.stockTotal = stockTotal; }
    public void setStockDisponible(int stockDisponible) { this.stockDisponible = stockDisponible; }
}
