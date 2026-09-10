package org.paginalibre8.dao.impl;

import java.util.List;
import org.paginalibre8.model.DetalleVenta;

public interface DetalleVentaDAO {
    boolean registrarDetalle(DetalleVenta detalle);
    boolean registrarDetalles(List<DetalleVenta> detalles);
    List<DetalleVenta> obtenerDetallesPorVenta(int idVenta);
}
