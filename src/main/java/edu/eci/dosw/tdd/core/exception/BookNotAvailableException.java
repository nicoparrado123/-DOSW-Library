package edu.eci.dosw.tdd.core.exception;

public class BookNotAvailableException extends Exception {
    public BookNotAvailableException(String mensaje) {
        super(mensaje);
    }
}
