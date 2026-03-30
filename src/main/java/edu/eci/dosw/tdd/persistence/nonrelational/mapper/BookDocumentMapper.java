package edu.eci.dosw.tdd.persistence.nonrelational.mapper;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.persistence.nonrelational.document.BookDocument;
import org.springframework.stereotype.Component;

@Component
public class BookDocumentMapper {
    public Book toDomain(BookDocument d) {
        int stock = d.getDisponibilidad() != null ? d.getDisponibilidad().getCopiasDisponibles() : 0;
        return new Book(d.getId(), d.getTitulo(), d.getAutor(), stock);
    }
    public BookDocument toDocument(Book b, int stockTotal, int stockDisponible) {
        BookDocument doc = new BookDocument();
        doc.setId(b.getId());
        doc.setTitulo(b.getTitulo());
        doc.setAutor(b.getAutor());
        BookDocument.Disponibilidad disp = new BookDocument.Disponibilidad();
        disp.setTotalCopias(stockTotal);
        disp.setCopiasDisponibles(stockDisponible);
        disp.setCopiasPrestadas(stockTotal - stockDisponible);
        disp.setStatus(stockDisponible > 0 ? "DISPONIBLE" : "NO_DISPONIBLE");
        doc.setDisponibilidad(disp);
        return doc;
    }
}
