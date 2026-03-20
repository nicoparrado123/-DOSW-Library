package edu.eci.dosw.tdd.controller.mapper;

import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.util.DateUtil;
import org.springframework.stereotype.Component;

@Component
public class LoanMapper {
    public LoanDTO toDTO(Loan prestamo) {
        LoanDTO dto = new LoanDTO();
        dto.setIdLibro(prestamo.getLibro().getId());
        dto.setTituloLibro(prestamo.getLibro().getTitulo());
        dto.setIdUsuario(prestamo.getUsuario().getId());
        dto.setNombreUsuario(prestamo.getUsuario().getNombre());
        dto.setLoanDate(DateUtil.format(prestamo.getFechaPrestamo()));
        dto.setReturnDate(DateUtil.format(prestamo.getFechaDevolucion()));
        dto.setEstado(prestamo.getEstado().name());
        return dto;
    }
}
