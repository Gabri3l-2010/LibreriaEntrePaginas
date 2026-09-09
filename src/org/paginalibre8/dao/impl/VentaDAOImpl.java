package org.paginalibre8.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.paginalibre8.model.Venta;
import org.paginalibre8.util.Conexion;

public class VentaDAOImpl implements VentaDAO {

    @Override
    public int registrarVenta(Venta venta) {
        if (venta == null) return -1;
        
        String sqlProc = "{call sp_registrar_venta(?, ?, ?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement call = conexion.prepareCall(sqlProc)) {
            call.setDouble(1, venta.getTotal());
            call.setInt(2, venta.getIdUsuario());
            call.setString(3, venta.getNitCliente());
            try (ResultSet rs = call.executeQuery()) {
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    venta.setId(idGenerado);
                    return idGenerado;
                }
            }
        } catch (SQLException e) {
            // Fallback SQL directo
            String sqlInsert = "INSERT INTO ventas (total, id_usuario, nit_cliente, fecha) VALUES (?, ?, ?, NOW())";
            try (Connection conexion = Conexion.getInstancia().conectar();
                 PreparedStatement ps = conexion.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                ps.setDouble(1, venta.getTotal());
                ps.setInt(2, venta.getIdUsuario());
                ps.setString(3, venta.getNitCliente());
                int filas = ps.executeUpdate();
                if (filas > 0) {
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) {
                            int idGenerado = keys.getInt(1);
                            venta.setId(idGenerado);
                            return idGenerado;
                        }
                    }
                }
            } catch (SQLException e2) {
                System.err.println("Error al registrar Venta: " + e2.getMessage());
            }
        }
        return -1;
    }

    @Override
    public Venta buscarPorId(int idVenta) {
        String sqlProc = "{call sp_buscar_venta(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement call = conexion.prepareCall(sqlProc)) {
            call.setInt(1, idVenta);
            try (ResultSet rs = call.executeQuery()) {
                if (rs.next()) {
                    return mapearVenta(rs);
                }
            }
        } catch (SQLException e) {
            String sqlFallback = "SELECT v.*, u.username AS username_usuario, c.nombre AS nombre_cliente "
                               + "FROM ventas v "
                               + "LEFT JOIN usuarios u ON v.id_usuario = u.id "
                               + "LEFT JOIN clientes c ON v.nit_cliente = c.nit "
                               + "WHERE v.id = ?";
            try (Connection conexion = Conexion.getInstancia().conectar();
                 PreparedStatement ps = conexion.prepareStatement(sqlFallback)) {
                ps.setInt(1, idVenta);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapearVenta(rs);
                    }
                }
            } catch (SQLException e2) {
                System.err.println("Error al buscar Venta por ID: " + e2.getMessage());
            }
        }
        return null;
    }

    @Override
    public List<Venta> listarVentas() {
        List<Venta> ventas = new ArrayList<>();
        String sqlProc = "{call sp_listar_ventas()}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement call = conexion.prepareCall(sqlProc);
             ResultSet rs = call.executeQuery()) {
            while (rs.next()) {
                ventas.add(mapearVenta(rs));
            }
        } catch (SQLException e) {
            String sqlFallback = "SELECT * FROM ventas ORDER BY id DESC";
            try (Connection conexion = Conexion.getInstancia().conectar();
                 PreparedStatement ps = conexion.prepareStatement(sqlFallback);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ventas.add(mapearVenta(rs));
                }
            } catch (SQLException e2) {
                System.err.println("Error al listar Ventas: " + e2.getMessage());
            }
        }
        return ventas;
    }

    private Venta mapearVenta(ResultSet rs) throws SQLException {
        Venta v = new Venta();
        v.setId(rs.getInt("id"));
        v.setTotal(rs.getDouble("total"));
        v.setIdUsuario(rs.getInt("id_usuario"));
        try {
            v.setFecha(rs.getTimestamp("fecha") != null ? rs.getTimestamp("fecha").toString() : "");
        } catch (SQLException ignored) {
            v.setFecha("");
        }
        try {
            v.setNitCliente(rs.getString("nit_cliente"));
        } catch (SQLException ignored) {}
        try {
            v.setUsernameUsuario(rs.getString("username_usuario"));
        } catch (SQLException ignored) {}
        try {
            v.setNombreCliente(rs.getString("nombre_cliente"));
        } catch (SQLException ignored) {}
        return v;
    }
}
