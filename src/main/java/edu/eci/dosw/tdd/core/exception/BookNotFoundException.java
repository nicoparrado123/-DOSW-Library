package edu.eci.dosw.tdd.core.exception;

public class BookNotFoundException extends Exception {
    public BookNotFoundException(String mensaje) {
        super(mensaje);
    }
}
