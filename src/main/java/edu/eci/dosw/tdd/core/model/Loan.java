package edu.eci.dosw.tdd.core.model;

import java.time.LocalDate;

public class Loan {
    private Book libro;
    private User usuario;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    private LoanStatus estado;

    public Loan(Book libro, User usuario, LocalDate fechaPrestamo) {
        this.libro = libro;
        this.usuario = usuario;
        this.fechaPrestamo = fechaPrestamo;
        this.estado = LoanStatus.ACTIVO;
    }

    public Book getLibro() { return libro; }
    public User getUsuario() { return usuario; }
    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public LocalDate getFechaDevolucion() { return fechaDevolucion; }
    public LoanStatus getEstado() { return estado; }
    public void setFechaDevolucion(LocalDate fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }
    public void setEstado(LoanStatus estado) { this.estado = estado; }
}
