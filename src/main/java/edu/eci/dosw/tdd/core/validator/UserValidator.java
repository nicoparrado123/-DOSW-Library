package edu.eci.dosw.tdd.core.validator;

import edu.eci.dosw.tdd.core.util.ValidationUtil;
import org.springframework.stereotype.Component;

@Component
public class UserValidator {
    public void validar(String id, String nombre) {
        if (!ValidationUtil.isValidId(id)) throw new IllegalArgumentException("id del usuario invalido");
        if (!ValidationUtil.isNotEmpty(nombre)) throw new IllegalArgumentException("nombre del usuario requerido");
    }

    public void validarCredenciales(String username, String password) {
        if (!ValidationUtil.isNotEmpty(username)) throw new IllegalArgumentException("username requerido");
        if (password == null || password.length() < 6) throw new IllegalArgumentException("password debe tener al menos 6 caracteres");
    }
}
