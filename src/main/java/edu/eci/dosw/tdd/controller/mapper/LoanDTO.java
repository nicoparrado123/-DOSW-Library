package edu.eci.dosw.tdd.controller.mapper;

public class LoanDTO {
    private String idLibro;
    private String tituloLibro;
    private String idUsuario;
    private String nombreUsuario;
    private String loanDate;
    private String returnDate;
    private String estado;

    public String getIdLibro() { return idLibro; }
    public String getTituloLibro() { return tituloLibro; }
    public String getIdUsuario() { return idUsuario; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getLoanDate() { return loanDate; }
    public String getReturnDate() { return returnDate; }
    public String getEstado() { return estado; }
    public void setIdLibro(String idLibro) { this.idLibro = idLibro; }
    public void setTituloLibro(String tituloLibro) { this.tituloLibro = tituloLibro; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
    public void setLoanDate(String loanDate) { this.loanDate = loanDate; }
    public void setReturnDate(String returnDate) { this.returnDate = returnDate; }
    public void setEstado(String estado) { this.estado = estado; }
}
