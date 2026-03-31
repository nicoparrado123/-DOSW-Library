package edu.eci.dosw.tdd.persistence.nonrelational.mapper;

import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.persistence.nonrelational.document.LoanDocument;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class LoanDocumentMapper {
    public Loan toDomain(LoanDocument d, Book libro, User usuario) {
        Loan loan = new Loan(libro, usuario, d.getFechaPrestamo());
        loan.setEstado(LoanStatus.valueOf(d.getEstado()));
        loan.setFechaDevolucion(d.getFechaDevolucion());
        return loan;
    }
    public LoanDocument toDocument(Loan loan) {
        LoanDocument doc = new LoanDocument();
        doc.setUsuarioId(loan.getUsuario().getId());
        doc.setLibroId(loan.getLibro().getId());
        doc.setFechaPrestamo(loan.getFechaPrestamo());
        doc.setFechaDevolucion(loan.getFechaDevolucion());
        doc.setEstado(loan.getEstado().name());
        List<LoanDocument.HistorialEntry> historial = new ArrayList<>();
        historial.add(new LoanDocument.HistorialEntry(loan.getEstado().name(), LocalDate.now()));
        doc.setHistorial(historial);
        return doc;
    }
}
