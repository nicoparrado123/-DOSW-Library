package edu.eci.dosw.tdd.persistence.nonrelational.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.util.List;

@Document(collection = "books")
public class BookDocument {

    @Id
    private String id;
    private String titulo;
    private String autor;
    private String isbn;
    private String tipoPublicacion;
    private LocalDate fechaPublicacion;
    private LocalDate fechaAgregado;
    private List<String> categorias;
    private Metadata metadata;
    private Disponibilidad disponibilidad;

    public static class Metadata {
        private int paginas;
        private String idioma;
        private String editorial;

        public int getPaginas() { return paginas; }
        public String getIdioma() { return idioma; }
        public String getEditorial() { return editorial; }
        public void setPaginas(int paginas) { this.paginas = paginas; }
        public void setIdioma(String idioma) { this.idioma = idioma; }
        public void setEditorial(String editorial) { this.editorial = editorial; }
    }

    public static class Disponibilidad {
        private String status;
        private int totalCopias;
        private int copiasDisponibles;
        private int copiasPrestadas;

        public String getStatus() { return status; }
        public int getTotalCopias() { return totalCopias; }
        public int getCopiasDisponibles() { return copiasDisponibles; }
        public int getCopiasPrestadas() { return copiasPrestadas; }
        public void setStatus(String status) { this.status = status; }
        public void setTotalCopias(int totalCopias) { this.totalCopias = totalCopias; }
        public void setCopiasDisponibles(int copiasDisponibles) { this.copiasDisponibles = copiasDisponibles; }
        public void setCopiasPrestadas(int copiasPrestadas) { this.copiasPrestadas = copiasPrestadas; }
    }

    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public String getIsbn() { return isbn; }
    public String getTipoPublicacion() { return tipoPublicacion; }
    public LocalDate getFechaPublicacion() { return fechaPublicacion; }
    public LocalDate getFechaAgregado() { return fechaAgregado; }
    public List<String> getCategorias() { return categorias; }
    public Metadata getMetadata() { return metadata; }
    public Disponibilidad getDisponibilidad() { return disponibilidad; }
    public void setId(String id) { this.id = id; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setAutor(String autor) { this.autor = autor; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setTipoPublicacion(String tipoPublicacion) { this.tipoPublicacion = tipoPublicacion; }
    public void setFechaPublicacion(LocalDate fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }
    public void setFechaAgregado(LocalDate fechaAgregado) { this.fechaAgregado = fechaAgregado; }
    public void setCategorias(List<String> categorias) { this.categorias = categorias; }
    public void setMetadata(Metadata metadata) { this.metadata = metadata; }
    public void setDisponibilidad(Disponibilidad disponibilidad) { this.disponibilidad = disponibilidad; }
}
