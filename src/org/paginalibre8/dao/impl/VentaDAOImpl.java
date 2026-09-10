package org.paginalibre8.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.paginalibre8.model.DetalleVenta;
import org.paginalibre8.model.Venta;
import org.paginalibre8.util.Conexion;

public class VentaDAOImpl implements VentaDAO {

    @Override
    public int registrarVenta(Venta venta) {
        if (venta == null) return -1;
        
        String sqlProc = "{call sp_insertarventa(?, ?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement call = conexion.prepareCall(sqlProc)) {
            call.setDouble(1, venta.getTotal());
            try {
                call.setLong(2, Long.parseLong(venta.getNitCliente()));
            } catch (NumberFormatException e) {
                call.setNull(2, java.sql.Types.BIGINT);
            }
            try (ResultSet rs = call.executeQuery()) {
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);
                    venta.setId(idGenerado);
                    return idGenerado;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al registrar Venta: " + e.getMessage());
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
            System.err.println("Error al buscar Venta por ID: " + e.getMessage());
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
            System.err.println("Error al listar Ventas: " + e.getMessage());
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
            System.err.println("Error al validar stock de venta: " + e.getMessage());
            return false;
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

            String sqlVenta = "{call sp_insertarventa(?, ?)}";
            int idVentaGenerado = -1;
            try (CallableStatement csVenta = conexion.prepareCall(sqlVenta)) {
                csVenta.setDouble(1, venta.getTotal());
                try {
                    csVenta.setLong(2, Long.parseLong(venta.getNitCliente()));
                } catch (NumberFormatException nfe) {
                    csVenta.setNull(2, java.sql.Types.BIGINT);
                }
                try (ResultSet keys = csVenta.executeQuery()) {
                    if (keys.next()) {
                        idVentaGenerado = keys.getInt(1);
                        venta.setId(idVentaGenerado);
                    } else {
                        idVentaGenerado = 1;
                    }
                }
            }

            String sqlDetalle = "{call sp_insertardetalleventa(?, ?)}";
            String sqlStock = "{call sp_actualizar_stock_libro(?, ?)}";

            try (CallableStatement csDetalle = conexion.prepareCall(sqlDetalle);
                 CallableStatement csStock = conexion.prepareCall(sqlStock)) {

                for (DetalleVenta d : venta.getDetalles()) {
                    d.setIdVenta(idVentaGenerado);
                    
                    csDetalle.setInt(1, idVentaGenerado);
                    csDetalle.setString(2, d.getIsbnLibro());
                    csDetalle.execute();

                    csStock.setString(1, d.getIsbnLibro());
                    csStock.setInt(2, d.getCantidad());
                    csStock.execute();
                }
            }

            String sqlMov = "{call sp_registrar_movimiento_inventario(?, ?, ?, ?, ?)}";
            try (CallableStatement csMov = conexion.prepareCall(sqlMov)) {
                for (DetalleVenta d : venta.getDetalles()) {
                    csMov.setString(1, d.getIsbnLibro());
                    csMov.setString(2, "VENTA");
                    csMov.setInt(3, d.getCantidad());
                    csMov.setInt(4, venta.getIdUsuario());
                    csMov.setString(5, "Venta #" + idVentaGenerado);
                    csMov.execute();
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
            System.err.println("Error al obtener ventas del día: " + e.getMessage());
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
            System.err.println("Error al obtener ventas del día por usuario: " + e.getMessage());
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
