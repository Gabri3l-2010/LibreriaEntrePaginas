package org.paginalibre8.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.paginalibre8.model.DetalleVenta;
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
            String sqlInsert = "INSERT INTO ventas (subtotal, total, id_usuario, cui_cliente, estado) VALUES (?, ?, ?, ?, 'COMPLETADA')";
            try (Connection conexion = Conexion.getInstancia().conectar();
                 PreparedStatement ps = conexion.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                ps.setDouble(1, venta.getTotal());
                ps.setDouble(2, venta.getTotal());
                ps.setInt(3, venta.getIdUsuario());
                try {
                    ps.setLong(4, Long.parseLong(venta.getNitCliente()));
                } catch (NumberFormatException nfe) {
                    ps.setNull(4, java.sql.Types.BIGINT);
                }
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
        String sqlProc = "{call sp_buscar_venta_por_id(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement call = conexion.prepareCall(sqlProc)) {
            call.setInt(1, idVenta);
            try (ResultSet rs = call.executeQuery()) {
                if (rs.next()) {
                    return mapearVenta(rs);
                }
            }
        } catch (SQLException e) {
            String sqlFallback = "SELECT v.id_venta AS id, v.*, v.fecha_venta AS fecha, v.cui_cliente AS nit_cliente, u.username AS username_usuario, CONCAT(c.nombre_cliente, ' ', c.apellido_cliente) AS nombre_cliente "
                               + "FROM ventas v "
                               + "LEFT JOIN usuarios u ON v.id_usuario = u.id_usuario "
                               + "LEFT JOIN clientes c ON v.cui_cliente = c.cui "
                               + "WHERE v.id_venta = ?";
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
            String sqlFallback = "SELECT id_venta AS id, v.*, fecha_venta AS fecha, cui_cliente AS nit_cliente FROM ventas v ORDER BY id_venta DESC";
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

    @Override
    public boolean validarStockVenta(Venta venta) {
        if (venta == null || venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            return false;
        }
        String sql = "{call sp_validar_stock_libro(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement call = conexion.prepareCall(sql)) {
            for (DetalleVenta d : venta.getDetalles()) {
                call.setString(1, d.getIsbnLibro());
                try (ResultSet rs = call.executeQuery()) {
                    if (rs.next()) {
                        int stockActual = rs.getInt("stock_actual");
                        if (stockActual < d.getCantidad()) {
                            System.err.println("Stock insuficiente para ISBN " + d.getIsbnLibro() + ". Disponible: " + stockActual + ", Requerido: " + d.getCantidad());
                            return false;
                        }
                    } else {
                        return false;
                    }
                }
            }
            return true;
        } catch (SQLException e) {
            String sqlFallback = "SELECT stock_actual FROM libros WHERE isbn = ?";
            try (Connection conexion = Conexion.getInstancia().conectar();
                 PreparedStatement ps = conexion.prepareStatement(sqlFallback)) {
                for (DetalleVenta d : venta.getDetalles()) {
                    ps.setString(1, d.getIsbnLibro());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            int stockActual = rs.getInt("stock_actual");
                            if (stockActual < d.getCantidad()) {
                                return false;
                            }
                        } else {
                            return false;
                        }
                    }
                }
                return true;
            } catch (SQLException e2) {
                System.err.println("Error al validar stock de venta: " + e2.getMessage());
                return false;
            }
        }
    }

    @Override
    public boolean registrarVentaTransaccional(Venta venta) {
        if (venta == null || venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
            return false;
        }

        if (!validarStockVenta(venta)) {
            return false;
        }

        Connection conexion = null;
        try {
            conexion = Conexion.getInstancia().conectar();
            conexion.setAutoCommit(false);

            String sqlVenta = "INSERT INTO ventas (subtotal, total, id_usuario, cui_cliente, estado) VALUES (?, ?, ?, ?, 'COMPLETADA')";
            int idVentaGenerado = -1;
            try (PreparedStatement psVenta = conexion.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {
                psVenta.setDouble(1, venta.getTotal());
                psVenta.setDouble(2, venta.getTotal());
                psVenta.setInt(3, venta.getIdUsuario());
                try {
                    psVenta.setLong(4, Long.parseLong(venta.getNitCliente()));
                } catch (NumberFormatException nfe) {
                    psVenta.setNull(4, java.sql.Types.BIGINT);
                }
                int filasVenta = psVenta.executeUpdate();
                if (filasVenta == 0) {
                    throw new SQLException("Falló la inserción del encabezado de la venta.");
                }
                try (ResultSet keys = psVenta.getGeneratedKeys()) {
                    if (keys.next()) {
                        idVentaGenerado = keys.getInt(1);
                        venta.setId(idVentaGenerado);
                    } else {
                        throw new SQLException("No se obtuvo la clave primaria generada para la venta.");
                    }
                }
            }

            String sqlDetalle = "INSERT INTO detalle_venta (id_venta, isbn, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
            String sqlStock = "UPDATE libros SET stock_actual = stock_actual - ? WHERE isbn = ? AND stock_actual >= ?";

            try (PreparedStatement psDetalle = conexion.prepareStatement(sqlDetalle);
                 PreparedStatement psStock = conexion.prepareStatement(sqlStock)) {

                for (DetalleVenta d : venta.getDetalles()) {
                    d.setIdVenta(idVentaGenerado);
                    
                    psDetalle.setInt(1, idVentaGenerado);
                    psDetalle.setString(2, d.getIsbnLibro());
                    psDetalle.setInt(3, d.getCantidad());
                    psDetalle.setDouble(4, d.getPrecioUnitario());
                    psDetalle.setDouble(5, d.getSubtotal());
                    psDetalle.executeUpdate();

                    psStock.setInt(1, d.getCantidad());
                    psStock.setString(2, d.getIsbnLibro());
                    psStock.setInt(3, d.getCantidad());
                    int stockActualizado = psStock.executeUpdate();
                    if (stockActualizado == 0) {
                        throw new SQLException("Stock insuficiente o error al descontar inventario del ISBN: " + d.getIsbnLibro());
                    }
                }
            }

            String sqlMov = "INSERT INTO movimientos_inventario (isbn, tipo_movimiento, cantidad, id_usuario, observacion) VALUES (?, 'VENTA', ?, ?, ?)";
            try (PreparedStatement psMov = conexion.prepareStatement(sqlMov)) {
                for (DetalleVenta d : venta.getDetalles()) {
                    psMov.setString(1, d.getIsbnLibro());
                    psMov.setInt(2, d.getCantidad());
                    psMov.setInt(3, venta.getIdUsuario());
                    psMov.setString(4, "Venta #" + idVentaGenerado);
                    psMov.executeUpdate();
                }
            }

            conexion.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Error en transacción de venta. Ejecutando rollback: " + e.getMessage());
            if (conexion != null) {
                try {
                    conexion.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error al realizar rollback: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (conexion != null) {
                try {
                    conexion.setAutoCommit(true);
                    conexion.close();
                } catch (SQLException ex) {
                    System.err.println("Error al cerrar conexión: " + ex.getMessage());
                }
            }
        }
    }

    @Override
    public List<Venta> obtenerVentasDelDia() {
        List<Venta> ventas = new ArrayList<>();
        String sqlProc = "{call sp_obtener_ventas_del_dia()}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement call = conexion.prepareCall(sqlProc);
             ResultSet rs = call.executeQuery()) {
            while (rs.next()) {
                ventas.add(mapearVenta(rs));
            }
        } catch (SQLException e) {
            String sqlFallback = "SELECT v.id_venta AS id, v.*, v.fecha_venta AS fecha, v.cui_cliente AS nit_cliente, u.username AS username_usuario, CONCAT(c.nombre_cliente, ' ', c.apellido_cliente) AS nombre_cliente "
                               + "FROM ventas v "
                               + "LEFT JOIN usuarios u ON v.id_usuario = u.id_usuario "
                               + "LEFT JOIN clientes c ON v.cui_cliente = c.cui "
                               + "WHERE DATE(v.fecha_venta) = CURDATE() ORDER BY v.id_venta DESC";
            try (Connection conexion = Conexion.getInstancia().conectar();
                 PreparedStatement ps = conexion.prepareStatement(sqlFallback);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ventas.add(mapearVenta(rs));
                }
            } catch (SQLException e2) {
                System.err.println("Error al obtener ventas del día: " + e2.getMessage());
            }
        }
        return ventas;
    }

    @Override
    public List<Venta> obtenerVentasDelDiaPorUsuario(int idUsuario) {
        List<Venta> ventas = new ArrayList<>();
        String sqlProc = "{call sp_obtener_ventas_del_dia_por_usuario(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement call = conexion.prepareCall(sqlProc)) {
            call.setInt(1, idUsuario);
            try (ResultSet rs = call.executeQuery()) {
                while (rs.next()) {
                    ventas.add(mapearVenta(rs));
                }
            }
        } catch (SQLException e) {
            String sqlFallback = "SELECT v.id_venta AS id, v.*, v.fecha_venta AS fecha, v.cui_cliente AS nit_cliente, u.username AS username_usuario, CONCAT(c.nombre_cliente, ' ', c.apellido_cliente) AS nombre_cliente "
                               + "FROM ventas v "
                               + "LEFT JOIN usuarios u ON v.id_usuario = u.id_usuario "
                               + "LEFT JOIN clientes c ON v.cui_cliente = c.cui "
                               + "WHERE DATE(v.fecha_venta) = CURDATE() AND v.id_usuario = ? ORDER BY v.id_venta DESC";
            try (Connection conexion = Conexion.getInstancia().conectar();
                 PreparedStatement ps = conexion.prepareStatement(sqlFallback)) {
                ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ventas.add(mapearVenta(rs));
                    }
                }
            } catch (SQLException e2) {
                System.err.println("Error al obtener ventas del día por usuario: " + e2.getMessage());
            }
        }
        return ventas;
    }

    private Venta mapearVenta(ResultSet rs) throws SQLException {
        Venta v = new Venta();
        try {
            v.setId(rs.getInt("id"));
        } catch (SQLException e) {
            v.setId(rs.getInt("id_venta"));
        }
        v.setTotal(rs.getDouble("total"));
        v.setIdUsuario(rs.getInt("id_usuario"));
        try {
            v.setFecha(rs.getTimestamp("fecha") != null ? rs.getTimestamp("fecha").toString() : "");
        } catch (SQLException e1) {
            try {
                v.setFecha(rs.getTimestamp("fecha_venta") != null ? rs.getTimestamp("fecha_venta").toString() : "");
            } catch (SQLException e2) {
                v.setFecha("");
            }
        }
        try {
            v.setNitCliente(rs.getString("nit_cliente"));
        } catch (SQLException e1) {
            try {
                v.setNitCliente(String.valueOf(rs.getLong("cui_cliente")));
            } catch (SQLException e2) {}
        }
        try {
            v.setUsernameUsuario(rs.getString("username_usuario"));
        } catch (SQLException ignored) {}
        try {
            v.setNombreCliente(rs.getString("nombre_cliente"));
        } catch (SQLException ignored) {}
        return v;
    }
}
