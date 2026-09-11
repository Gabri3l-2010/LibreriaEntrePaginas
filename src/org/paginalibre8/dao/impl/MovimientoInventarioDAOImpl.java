package org.paginalibre8.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.paginalibre8.model.MovimientoInventario;
import org.paginalibre8.util.Conexion;

public class MovimientoInventarioDAOImpl implements MovimientoInventarioDAO {

    private MovimientoInventario mapearMovimiento(ResultSet rs) throws SQLException {
        MovimientoInventario m = new MovimientoInventario();
        m.setIdMovimiento(rs.getInt("id_movimiento"));
        m.setIsbn(rs.getString("isbn"));
        m.setTipoMovimiento(rs.getString("tipo_movimiento"));
        m.setCantidad(rs.getInt("cantidad"));
        m.setFechaMovimiento(rs.getTimestamp("fecha_movimiento"));
        m.setIdUsuario(rs.getInt("id_usuario"));
        m.setObservacion(rs.getString("observacion"));

        try {
            m.setTituloLibro(rs.getString("titulo_libro"));
        } catch (SQLException ignored) {}

        try {
            m.setUsernameUsuario(rs.getString("username_usuario"));
        } catch (SQLException ignored) {}

        return m;
    }

    @Override
    public boolean registrarMovimiento(MovimientoInventario movimiento) {
        String consulta = "{call sp_registrar_movimiento_inventario(?, ?, ?, ?, ?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement call = conexion.prepareCall(consulta)) {
            call.setString(1, movimiento.getIsbn());
            call.setString(2, movimiento.getTipoMovimiento());
            call.setInt(3, movimiento.getCantidad());
            call.setInt(4, movimiento.getIdUsuario());
            call.setString(5, movimiento.getObservacion());
            return call.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al registrar movimiento con procedimiento almacenado: " + e.getMessage());
            String sql = "INSERT INTO movimientos_inventario (isbn, tipo_movimiento, cantidad, id_usuario, observacion) VALUES (?, ?, ?, ?, ?)";
            try (Connection conexion = Conexion.getInstancia().conectar();
                 PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setString(1, movimiento.getIsbn());
                ps.setString(2, movimiento.getTipoMovimiento());
                ps.setInt(3, movimiento.getCantidad());
                ps.setInt(4, movimiento.getIdUsuario());
                ps.setString(5, movimiento.getObservacion());
                return ps.executeUpdate() > 0;
            } catch (SQLException ex) {
                System.err.println("Error al registrar movimiento de inventario fallback: " + ex.getMessage());
                return false;
            }
        }
    }

    @Override
    public boolean registrarIngresoTransaccional(MovimientoInventario movimiento) {
        if (movimiento == null || movimiento.getIsbn() == null
                || movimiento.getIsbn().trim().isEmpty()
                || movimiento.getCantidad() <= 0 || movimiento.getIdUsuario() <= 0) {
            return false;
        }
        Connection conexion = null;
        try {
            conexion = Conexion.getInstancia().conectar();
            conexion.setAutoCommit(false);

            String sqlStock = "UPDATE libros SET stock_actual = stock_actual + ? WHERE isbn = ?";
            try (PreparedStatement psStock = conexion.prepareStatement(sqlStock)) {
                psStock.setInt(1, movimiento.getCantidad());
                psStock.setString(2, movimiento.getIsbn());
                int filasStock = psStock.executeUpdate();
                if (filasStock == 0) {
                    conexion.rollback();
                    return false;
                }
            }

            String sqlMov = "INSERT INTO movimientos_inventario (isbn, tipo_movimiento, cantidad, id_usuario, observacion) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement psMov = conexion.prepareStatement(sqlMov)) {
                psMov.setString(1, movimiento.getIsbn());
                psMov.setString(2, movimiento.getTipoMovimiento() != null ? movimiento.getTipoMovimiento() : "INGRESO");
                psMov.setInt(3, movimiento.getCantidad());
                psMov.setInt(4, movimiento.getIdUsuario());
                psMov.setString(5, movimiento.getObservacion());
                psMov.executeUpdate();
            }

            conexion.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error en transacción de ingreso de inventario: " + e.getMessage());
            if (conexion != null) {
                try {
                    conexion.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error al hacer rollback: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (conexion != null) {
                try {
                    conexion.setAutoCommit(true);
                    conexion.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar conexión: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public List<MovimientoInventario> listarMovimientos() {
        List<MovimientoInventario> lista = new ArrayList<>();
        String sql = "SELECT m.*, l.titulo AS titulo_libro, u.username AS username_usuario "
                   + "FROM movimientos_inventario m "
                   + "LEFT JOIN libros l ON m.isbn = l.isbn "
                   + "LEFT JOIN usuarios u ON m.id_usuario = u.id_usuario "
                   + "ORDER BY m.id_movimiento DESC";
        try (Connection conexion = Conexion.getInstancia().conectar();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearMovimiento(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar movimientos de inventario: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public List<MovimientoInventario> listarMovimientosPorLibro(String isbn) {
        List<MovimientoInventario> lista = new ArrayList<>();
        String sql = "SELECT m.*, l.titulo AS titulo_libro, u.username AS username_usuario "
                   + "FROM movimientos_inventario m "
                   + "LEFT JOIN libros l ON m.isbn = l.isbn "
                   + "LEFT JOIN usuarios u ON m.id_usuario = u.id_usuario "
                   + "WHERE m.isbn = ? "
                   + "ORDER BY m.id_movimiento DESC";
        try (Connection conexion = Conexion.getInstancia().conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearMovimiento(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar movimientos por libro: " + e.getMessage());
        }
        return lista;
    }
}
