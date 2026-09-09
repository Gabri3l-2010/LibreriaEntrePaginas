package org.paginalibre8.model;

import java.util.ArrayList;
import java.util.List;

public class Venta {

    private int id;
    private String fecha;
    private double total;
    private int idUsuario;
    private String usernameUsuario;
    private String nitCliente;
    private String nombreCliente;
    private List<DetalleVenta> detalles;

    public Venta() {
        this.detalles = new ArrayList<>();
    }

    public Venta(int id, String fecha, double total, int idUsuario, String nitCliente) {
        this.id = id;
        this.fecha = fecha;
        this.total = total;
        this.idUsuario = idUsuario;
        this.nitCliente = nitCliente;
        this.detalles = new ArrayList<>();
    }

    public Venta(int idUsuario, String nitCliente) {
        this.idUsuario = idUsuario;
        this.nitCliente = nitCliente;
        this.detalles = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsernameUsuario() {
        return usernameUsuario;
    }

    public void setUsernameUsuario(String usernameUsuario) {
        this.usernameUsuario = usernameUsuario;
    }

    public String getNitCliente() {
        return nitCliente;
    }

    public void setNitCliente(String nitCliente) {
        this.nitCliente = nitCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
        calcularTotal();
    }

    public void agregarDetalle(DetalleVenta detalle) {
        if (this.detalles == null) {
            this.detalles = new ArrayList<>();
        }
        this.detalles.add(detalle);
        calcularTotal();
    }

    public void eliminarDetalle(DetalleVenta detalle) {
        if (this.detalles != null) {
            this.detalles.remove(detalle);
            calcularTotal();
        }
    }

    public void calcularTotal() {
        double suma = 0;
        if (this.detalles != null) {
            for (DetalleVenta d : this.detalles) {
                suma += d.getSubtotal();
            }
        }
        this.total = Math.round(suma * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return "Venta #" + id + " - Q" + total;
    }
}
