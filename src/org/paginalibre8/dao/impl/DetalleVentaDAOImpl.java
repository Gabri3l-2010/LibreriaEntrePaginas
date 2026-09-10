package org.paginalibre8.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.paginalibre8.model.DetalleVenta;
import org.paginalibre8.util.Conexion;

public class DetalleVentaDAOImpl implements DetalleVentaDAO {

    @Override
    public boolean registrarDetalle(DetalleVenta detalle) {
        if (detalle == null) return false;

        String sqlProc = "{call sp_registrar_detalle_venta(?, ?, ?, ?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement call = conexion.prepareCall(sqlProc)) {
            call.setInt(1, detalle.getIdVenta());
            call.setString(2, detalle.getIsbnLibro());
            call.setInt(3, detalle.getCantidad());
            call.setDouble(4, detalle.getPrecioUnitario());
            return call.executeUpdate() > 0;
        } catch (SQLException e) {
            String sqlInsert = "INSERT INTO detalle_venta (id_venta, isbn, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
            try (Connection conexion = Conexion.getInstancia().conectar();
                 PreparedStatement ps = conexion.prepareStatement(sqlInsert)) {
                ps.setInt(1, detalle.getIdVenta());
                ps.setString(2, detalle.getIsbnLibro());
                ps.setInt(3, detalle.getCantidad());
                ps.setDouble(4, detalle.getPrecioUnitario());
                ps.setDouble(5, detalle.getSubtotal());
                return ps.executeUpdate() > 0;
            } catch (SQLException e2) {
                System.err.println("Error al registrar DetalleVenta: " + e2.getMessage());
                return false;
            }
        }
    }

    @Override
    public boolean registrarDetalles(List<DetalleVenta> detalles) {
        if (detalles == null || detalles.isEmpty()) return false;
        boolean exitoTotal = true;
        for (DetalleVenta d : detalles) {
            if (!registrarDetalle(d)) {
                exitoTotal = false;
            }
        }
        return exitoTotal;
    }

    @Override
    public List<DetalleVenta> obtenerDetallesPorVenta(int idVenta) {
        List<DetalleVenta> lista = new ArrayList<>();
        String sqlProc = "{call sp_obtener_detalles_venta(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement call = conexion.prepareCall(sqlProc)) {
            call.setInt(1, idVenta);
            try (ResultSet rs = call.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearDetalle(rs));
                }
            }
        } catch (SQLException e) {
            String sqlFallback = "SELECT d.id_detalle AS id, d.*, d.isbn AS isbn_libro, l.titulo AS titulo_libro "
                               + "FROM detalle_venta d "
                               + "LEFT JOIN libros l ON d.isbn = l.isbn "
                               + "WHERE d.id_venta = ?";
            try (Connection conexion = Conexion.getInstancia().conectar();
                 PreparedStatement ps = conexion.prepareStatement(sqlFallback)) {
                ps.setInt(1, idVenta);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(mapearDetalle(rs));
                    }
                }
            } catch (SQLException e2) {
                System.err.println("Error al obtener DetallesVenta por idVenta: " + e2.getMessage());
            }
        }
        return lista;
    }

    private DetalleVenta mapearDetalle(ResultSet rs) throws SQLException {
        DetalleVenta d = new DetalleVenta();
        try {
            d.setId(rs.getInt("id"));
        } catch (SQLException ignored) {
            d.setId(rs.getInt("id_detalle"));
        }
        d.setIdVenta(rs.getInt("id_venta"));
        try {
            d.setIsbnLibro(rs.getString("isbn_libro"));
        } catch (SQLException ignored) {
            d.setIsbnLibro(rs.getString("isbn"));
        }
        d.setCantidad(rs.getInt("cantidad"));
        d.setPrecioUnitario(rs.getDouble("precio_unitario"));
        try {
            d.setSubtotal(rs.getDouble("subtotal"));
        } catch (SQLException ignored) {
            d.calcularSubtotal();
        }
        try {
            d.setTituloLibro(rs.getString("titulo_libro"));
        } catch (SQLException ignored) {
            d.setTituloLibro("Libro ISBN: " + d.getIsbnLibro());
        }
        return d;
    }
}
