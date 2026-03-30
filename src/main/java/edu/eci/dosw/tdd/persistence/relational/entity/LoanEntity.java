package edu.eci.dosw.tdd.persistence.relational.entity;

import edu.eci.dosw.tdd.core.model.LoanStatus;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "loans")
public class LoanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private edu.eci.dosw.tdd.persistence.relational.entity.BookEntity libro;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private edu.eci.dosw.tdd.persistence.relational.entity.UserEntity usuario;

    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;

    @Enumerated(EnumType.STRING)
    private LoanStatus estado;

    public LoanEntity() {}

    public LoanEntity(BookEntity libro, UserEntity usuario, LocalDate fechaPrestamo) {
        this.libro = libro;
        this.usuario = usuario;
        this.fechaPrestamo = fechaPrestamo;
        this.estado = LoanStatus.ACTIVO;
    }

    public Long getId() { return id; }
    public BookEntity getLibro() { return libro; }
    public UserEntity getUsuario() { return usuario; }
    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public LocalDate getFechaDevolucion() { return fechaDevolucion; }
    public LoanStatus getEstado() { return estado; }
    public void setFechaDevolucion(LocalDate fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }
    public void setEstado(LoanStatus estado) { this.estado = estado; }
}
