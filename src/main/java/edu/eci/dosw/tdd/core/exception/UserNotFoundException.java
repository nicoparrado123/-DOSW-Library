package edu.eci.dosw.tdd.core.exception;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(String mensaje) {
        super(mensaje);
    }
}
