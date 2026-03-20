package edu.eci.dosw.tdd;

import edu.eci.dosw.tdd.controller.mapper.BookDTO;
import edu.eci.dosw.tdd.controller.mapper.BookMapper;
import edu.eci.dosw.tdd.controller.mapper.LoanDTO;
import edu.eci.dosw.tdd.controller.mapper.LoanMapper;
import edu.eci.dosw.tdd.controller.mapper.UserDTO;
import edu.eci.dosw.tdd.controller.mapper.UserMapper;
import edu.eci.dosw.tdd.core.model.Book;
import edu.eci.dosw.tdd.core.model.Loan;
import edu.eci.dosw.tdd.core.model.LoanStatus;
import edu.eci.dosw.tdd.core.model.User;
import edu.eci.dosw.tdd.core.util.DateUtil;
import edu.eci.dosw.tdd.core.util.ValidationUtil;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ModelAndUtilTest {

    @Test
    void bookGuardaAtributosCorrectamente() {
        Book libro = new Book("clean-001", "Clean Code", "Robert Martin");
        assertEquals("clean-001", libro.getId());
        assertEquals("Clean Code", libro.getTitulo());
        assertEquals("Robert Martin", libro.getAutor());
    }

    @Test
    void bookSettersActualizanValores() {
        Book libro = new Book();
        libro.setId("ddd-001");
        libro.setTitulo("Domain Driven Design");
        libro.setAutor("Eric Evans");
        assertEquals("ddd-001", libro.getId());
        assertEquals("Domain Driven Design", libro.getTitulo());
        assertEquals("Eric Evans", libro.getAutor());
    }

    @Test
    void userGuardaNombreEId() {
        User usuario = new User("nico-001", "Nicolas");
        assertEquals("nico-001", usuario.getId());
        assertEquals("Nicolas", usuario.getNombre());
    }

    @Test
    void userSettersActualizanValores() {
        User usuario = new User();
        usuario.setId("ana-001");
        usuario.setNombre("Ana");
        assertEquals("ana-001", usuario.getId());
        assertEquals("Ana", usuario.getNombre());
    }

    @Test
    void loanNuevoEstaActivoSinFechaDevolucion() {
        Book libro = new Book("clean-001", "Clean Code", "Martin");
        User usuario = new User("nico-001", "Nicolas");
        Loan prestamo = new Loan(libro, usuario, LocalDate.now());
        assertEquals(LoanStatus.ACTIVO, prestamo.getEstado());
        assertNull(prestamo.getFechaDevolucion());
        assertNotNull(prestamo.getFechaPrestamo());
    }

    @Test
    void loanDevueltoActualizaEstadoYFecha() {
        Loan prestamo = new Loan(
                new Book("clean-001", "Clean Code", "Martin"),
                new User("nico-001", "Nicolas"),
                LocalDate.now()
        );
        prestamo.setEstado(LoanStatus.DEVUELTO);
        prestamo.setFechaDevolucion(LocalDate.of(2024, 6, 20));
        assertEquals(LoanStatus.DEVUELTO, prestamo.getEstado());
        assertEquals(LocalDate.of(2024, 6, 20), prestamo.getFechaDevolucion());
    }

    @Test
    void loanGuardaReferenciaAlLibroYUsuario() {
        Book libro = new Book("clean-001", "Clean Code", "Martin");
        User usuario = new User("nico-001", "Nicolas");
        Loan prestamo = new Loan(libro, usuario, LocalDate.now());
        assertEquals(libro, prestamo.getLibro());
        assertEquals(usuario, prestamo.getUsuario());
    }

    @Test
    void loanStatusTieneActivoYDevuelto() {
        assertEquals(LoanStatus.ACTIVO, LoanStatus.valueOf("ACTIVO"));
        assertEquals(LoanStatus.DEVUELTO, LoanStatus.valueOf("DEVUELTO"));
        assertEquals(2, LoanStatus.values().length);
    }

    @Test
    void dateUtilFormateaFechaEnFormatoEspanol() {
        assertEquals("20/06/2024", DateUtil.format(LocalDate.of(2024, 6, 20)));
    }

    @Test
    void dateUtilRetornaCadenaVaciaConNull() {
        assertEquals("", DateUtil.format(null));
    }

    @Test
    void validationUtilDetectaCadenasVaciasYNulas() {
        assertFalse(ValidationUtil.isNotEmpty(null));
        assertFalse(ValidationUtil.isNotEmpty(""));
        assertFalse(ValidationUtil.isNotEmpty("   "));
        assertTrue(ValidationUtil.isNotEmpty("nico"));
    }

    @Test
    void validationUtilValidaFormatoDeId() {
        assertTrue(ValidationUtil.isValidId("nico-001"));
        assertFalse(ValidationUtil.isValidId(null));
        assertFalse(ValidationUtil.isValidId("  "));
        assertFalse(ValidationUtil.isValidId("id invalido"));
        assertFalse(ValidationUtil.isValidId("id@raro"));
    }

    @Test
    void bookMapperConvierteEntreModelYDTO() {
        Book libro = new Book("clean-001", "Clean Code", "Robert Martin");
        BookMapper mapper = new BookMapper();

        BookDTO dto = mapper.toDTO(libro);
        assertEquals("clean-001", dto.getId());
        assertEquals("Clean Code", dto.getTitulo());
        assertEquals("Robert Martin", dto.getAutor());

        Book desdeDTO = mapper.toModel(dto);
        assertEquals("clean-001", desdeDTO.getId());
        assertEquals("Clean Code", desdeDTO.getTitulo());
    }

    @Test
    void userMapperConvierteEntreModelYDTO() {
        User usuario = new User("nico-001", "Nicolas");
        UserMapper mapper = new UserMapper();

        UserDTO dto = mapper.toDTO(usuario);
        assertEquals("nico-001", dto.getId());
        assertEquals("Nicolas", dto.getNombre());

        User desdeDTO = mapper.toModel(dto);
        assertEquals("nico-001", desdeDTO.getId());
        assertEquals("Nicolas", desdeDTO.getNombre());
    }

    @Test
    void loanMapperConviertePrestamoActivoADTO() {
        Book libro = new Book("clean-001", "Clean Code", "Robert Martin");
        User usuario = new User("nico-001", "Nicolas");
        Loan prestamo = new Loan(libro, usuario, LocalDate.of(2024, 6, 1));

        LoanDTO dto = new LoanMapper().toDTO(prestamo);
        assertEquals("clean-001", dto.getIdLibro());
        assertEquals("Clean Code", dto.getTituloLibro());
        assertEquals("nico-001", dto.getIdUsuario());
        assertEquals("Nicolas", dto.getNombreUsuario());
        assertEquals("01/06/2024", dto.getLoanDate());
        assertEquals("", dto.getReturnDate());
        assertEquals("ACTIVO", dto.getEstado());
    }

    @Test
    void loanMapperConviertePrestamoDevueltoADTO() {
        Loan prestamo = new Loan(
                new Book("clean-001", "Clean Code", "Martin"),
                new User("nico-001", "Nicolas"),
                LocalDate.of(2024, 6, 1)
        );
        prestamo.setEstado(LoanStatus.DEVUELTO);
        prestamo.setFechaDevolucion(LocalDate.of(2024, 6, 15));

        LoanDTO dto = new LoanMapper().toDTO(prestamo);
        assertEquals("15/06/2024", dto.getReturnDate());
        assertEquals("DEVUELTO", dto.getEstado());
    }
}
