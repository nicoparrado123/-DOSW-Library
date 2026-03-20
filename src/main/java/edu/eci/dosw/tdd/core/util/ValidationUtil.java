package edu.eci.dosw.tdd.core.util;

public class ValidationUtil {
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidId(String id) {
        return isNotEmpty(id) && id.matches("^[a-zA-Z0-9\\-]+$");
    }
}
