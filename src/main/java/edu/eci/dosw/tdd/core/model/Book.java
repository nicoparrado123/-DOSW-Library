package edu.eci.dosw.tdd.core.model;

public class Book {
    private String id;
    private String title;
    private boolean available;

    public Book(String id, String title) {
        this.id = id;
        this.title = title;
        this.available = true;
    }
}
