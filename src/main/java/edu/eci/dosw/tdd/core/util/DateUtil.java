package edu.eci.dosw.tdd.core.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateUtil {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ROOT);

    public static String format(LocalDate date) {
        return date != null ? date.format(FORMAT) : "";
    }
}
