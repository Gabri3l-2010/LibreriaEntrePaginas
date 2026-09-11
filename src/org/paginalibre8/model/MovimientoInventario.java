package org.paginalibre8.model;

import java.sql.Timestamp;

public class MovimientoInventario {
    private int idMovimiento;
    private String isbn;
    private String tipoMovimiento; // INGRESO, VENTA, MERMA, TRASLADO, DEVOLUCION, AJUSTE
    private int cantidad;
    private Timestamp fechaMovimiento;
    private int idUsuario;
    private String observacion;

    // Campos auxiliares para vistas / UI
    private String tituloLibro;
    private String usernameUsuario;

    public MovimientoInventario() {}

    public MovimientoInventario(String isbn, String tipoMovimiento, int cantidad, int idUsuario, String observacion) {
        this.isbn = isbn;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidad = cantidad;
        this.idUsuario = idUsuario;
        this.observacion = observacion;
    }

    public int getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Timestamp getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(Timestamp fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getTituloLibro() {
        return tituloLibro;
    }

    public void setTituloLibro(String tituloLibro) {
        this.tituloLibro = tituloLibro;
    }

    public String getUsernameUsuario() {
        return usernameUsuario;
    }

    public void setUsernameUsuario(String usernameUsuario) {
        this.usernameUsuario = usernameUsuario;
    }
}
