package edu.eci.dosw.tdd.core.validator;

import edu.eci.dosw.tdd.core.util.ValidationUtil;
import org.springframework.stereotype.Component;

@Component
public class LoanValidator {
    public void validar(String idUsuario, String idLibro) {
        if (!ValidationUtil.isValidId(idUsuario)) throw new IllegalArgumentException("id del usuario invalido");
        if (!ValidationUtil.isValidId(idLibro)) throw new IllegalArgumentException("id del libro invalido");
    }
}
