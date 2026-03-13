package edu.eci.dosw.tdd.core.exception;

public class LoanLimitExceededException extends Exception {
    public LoanLimitExceededException(String message) {
        super(message);
    }
}
