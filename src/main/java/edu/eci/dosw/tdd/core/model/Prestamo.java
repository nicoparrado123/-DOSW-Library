package edu.eci.dosw.tdd.core.model;

import java.time.LocalDate;

public class Prestamo {
    private Libro libro;
    private Usuario usuario;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    private EstadoPrestamo estado;

    public Prestamo(Libro libro, Usuario usuario, LocalDate fechaPrestamo) {
        this.libro = libro;
        this.usuario = usuario;
        this.fechaPrestamo = fechaPrestamo;
        this.estado = EstadoPrestamo.ACTIVO;
    }

    public Libro getLibro() { return libro; }
    public Usuario getUsuario() { return usuario; }
    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public LocalDate getFechaDevolucion() { return fechaDevolucion; }
    public EstadoPrestamo getEstado() { return estado; }
    public void setFechaDevolucion(LocalDate fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }
    public void setEstado(EstadoPrestamo estado) { this.estado = estado; }
}
