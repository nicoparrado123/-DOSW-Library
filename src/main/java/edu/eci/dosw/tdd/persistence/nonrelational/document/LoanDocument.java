package edu.eci.dosw.tdd.persistence.nonrelational.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.util.List;

@Document(collection = "loans")
public class LoanDocument {

    @Id
    private String id;
    private String usuarioId;
    private String libroId;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    private String estado;
    private List<HistorialEntry> historial;

    public static class HistorialEntry {
        private String status;
        private LocalDate fecha;

        public HistorialEntry() {}

        public HistorialEntry(String status, LocalDate fecha) {
            this.status = status;
            this.fecha = fecha;
        }

        public String getStatus() { return status; }
        public LocalDate getFecha() { return fecha; }
        public void setStatus(String status) { this.status = status; }
        public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    }

    public String getId() { return id; }
    public String getUsuarioId() { return usuarioId; }
    public String getLibroId() { return libroId; }
    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public LocalDate getFechaDevolucion() { return fechaDevolucion; }
    public String getEstado() { return estado; }
    public List<HistorialEntry> getHistorial() { return historial; }
    public void setId(String id) { this.id = id; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }
    public void setLibroId(String libroId) { this.libroId = libroId; }
    public void setFechaPrestamo(LocalDate fechaPrestamo) { this.fechaPrestamo = fechaPrestamo; }
    public void setFechaDevolucion(LocalDate fechaDevolucion) { this.fechaDevolucion = fechaDevolucion; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setHistorial(List<HistorialEntry> historial) { this.historial = historial; }
}
