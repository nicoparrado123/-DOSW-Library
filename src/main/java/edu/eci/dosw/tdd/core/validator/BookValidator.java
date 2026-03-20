package edu.eci.dosw.tdd.core.validator;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.util.ValidationUtil;
import org.springframework.stereotype.Component;

@Component
public class BookValidator {
    public void validar(Book libro) {
        if (!ValidationUtil.isValidId(libro.getId())) throw new IllegalArgumentException("id del libro invalido");
        if (!ValidationUtil.isNotEmpty(libro.getTitulo())) throw new IllegalArgumentException("titulo del libro requerido");
        if (!ValidationUtil.isNotEmpty(libro.getAutor())) throw new IllegalArgumentException("autor del libro requerido");
    }
}
