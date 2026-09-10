package org.paginalibre8.model;

public class DetalleVenta {

    private int id;
    private int idVenta;
    private String isbnLibro;
    private String tituloLibro;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    public DetalleVenta() {
    }

    public DetalleVenta(int id, int idVenta, String isbnLibro, String tituloLibro,
                        int cantidad, double precioUnitario) {
        this.id = id;
        this.idVenta = idVenta;
        this.isbnLibro = isbnLibro;
        this.tituloLibro = tituloLibro;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        calcularSubtotal();
    }

    public DetalleVenta(String isbnLibro, String tituloLibro, int cantidad, double precioUnitario) {
        this.isbnLibro = isbnLibro;
        this.tituloLibro = tituloLibro;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        calcularSubtotal();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public String getIsbnLibro() {
        return isbnLibro;
    }

    public void setIsbnLibro(String isbnLibro) {
        this.isbnLibro = isbnLibro;
    }

    public String getTituloLibro() {
        return tituloLibro;
    }

    public void setTituloLibro(String tituloLibro) {
        this.tituloLibro = tituloLibro;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        calcularSubtotal();
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
        calcularSubtotal();
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public void calcularSubtotal() {
        this.subtotal = Math.round((this.cantidad * this.precioUnitario) * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return tituloLibro + " x" + cantidad + " (Subtotal: Q" + subtotal + ")";
    }
}
