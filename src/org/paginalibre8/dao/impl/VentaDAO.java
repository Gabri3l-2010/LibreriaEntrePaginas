package org.paginalibre8.dao.impl;

import java.util.List;
import org.paginalibre8.model.Venta;

public interface VentaDAO {
    int registrarVenta(Venta venta);
    boolean registrarVentaTransaccional(Venta venta);
    boolean validarStockVenta(Venta venta);
    Venta buscarPorId(int idVenta);
    List<Venta> listarVentas();
    List<Venta> obtenerVentasDelDia();
    List<Venta> obtenerVentasDelDiaPorUsuario(int idUsuario);
}
