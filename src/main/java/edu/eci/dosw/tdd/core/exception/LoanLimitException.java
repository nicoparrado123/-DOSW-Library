package edu.eci.dosw.tdd.core.exception;

public class LoanLimitException extends Exception {
    public LoanLimitException(String mensaje) {
        super(mensaje);
    }
}
