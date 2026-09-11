package org.paginalibre8.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.paginalibre8.model.Libro;
import org.paginalibre8.util.Conexion;

/**
 * Implementación DAO para la gestión de la entidad Libro en la base de datos.
 * Utiliza llamadas a procedimientos almacenados.
 */
public class LibroDAOImpl implements LibroDAO {

    /**
     * Mapea una fila del ResultSet a un objeto Libro.
     */
    private Libro mapearLibro(ResultSet rs) throws SQLException {
        Libro l = new Libro();
        l.setIsbn(rs.getString("isbn"));
        l.setTitulo(rs.getString("titulo"));

        try {
            l.setAutor(rs.getString("autor"));
        } catch (SQLException ignored) {
            try {
                l.setAutor(rs.getString("nombre_autor"));
            } catch (SQLException ignored2) {
                l.setAutor("Sin autor");
            }
        }

        try {
            l.setFechaPublicacion(rs.getDate("fecha_publicacion") != null
                    ? rs.getDate("fecha_publicacion").toString() : "");
        } catch (SQLException ignored) {
            l.setFechaPublicacion("");
        }

        try {
            l.setPrecio(rs.getDouble("precio"));
        } catch (SQLException ignored) {
            l.setPrecio(0.0);
        }

        try {
            l.setStock(rs.getInt("stock_actual"));
        } catch (SQLException ignored) {
            try {
                l.setStock(rs.getInt("stock"));
            } catch (SQLException ignored2) {
                l.setStock(0);
            }
        }

        try {
            l.setStockMinimo(rs.getInt("stock_minimo"));
        } catch (SQLException ignored) {
            l.setStockMinimo(5);
        }

        try {
            l.setActivo(rs.getBoolean("activo"));
        } catch (SQLException ignored) {
            l.setActivo(true);
        }

        try {
            l.setIdCategoria(rs.getInt("id_categoria"));
        } catch (SQLException ignored) {}

        try {
            l.setCategoriaNombre(rs.getString("nombre_categoria"));
        } catch (SQLException ignored) {}

        try {
            l.setNitEditorial(rs.getString("nit_editorial"));
        } catch (SQLException ignored) {}

        try {
            l.setEditorialNombre(rs.getString("nombre_editorial"));
        } catch (SQLException ignored) {}

        return l;
    }

    @Override
    public List<Libro> ListarTodos() {
        List<Libro> libros = new ArrayList<>();
        String consulta = "{call sp_listarlibros()}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement consultaCall = conexion.prepareCall(consulta);
             ResultSet tablaResultado = consultaCall.executeQuery()) {
            while (tablaResultado.next()) {
                libros.add(mapearLibro(tablaResultado));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar Libros: " + e.getMessage());
        }
        return libros;
    }

    @Override
    public boolean crear(Libro libro) {
        String consulta = "{call sp_insertarlibro(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement consultaCall = conexion.prepareCall(consulta)) {
            consultaCall.setString(1, libro.getIsbn());
            consultaCall.setString(2, libro.getTitulo());
            consultaCall.setDate(3, Date.valueOf(libro.getFechaPublicacion()));
            consultaCall.setDouble(4, libro.getPrecio());
            consultaCall.setInt(5, libro.getIdCategoria());
            consultaCall.setString(6, libro.getNitEditorial());
            consultaCall.setInt(7, libro.getStock());
            consultaCall.setInt(8, libro.getStockMinimo());
            consultaCall.setBoolean(9, libro.isActivo());
            return consultaCall.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al crear Libro: " + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            System.err.println("Fecha de publicación inválida, use el formato AAAA-MM-DD: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Libro buscarPorId(String isbn) {
        return buscarPorIsbn(isbn);
    }

    @Override
    public Libro buscarPorIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return null;
        }
        String consultaSQL = "{call sp_buscarlibro(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement consultaCall = conexion.prepareCall(consultaSQL)) {
            consultaCall.setString(1, isbn.trim());
            try (ResultSet tablaResultado = consultaCall.executeQuery()) {
                if (tablaResultado.next()) {
                    return mapearLibro(tablaResultado);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar Libro por ISBN: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Libro> buscarPorTitulo(String titulo) {
        List<Libro> resultados = new ArrayList<>();
        if (titulo == null || titulo.trim().isEmpty()) {
            return ListarTodos();
        }
        String sql = "{call sp_buscar_libros_por_titulo(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement call = conexion.prepareCall(sql)) {
            call.setString(1, titulo.trim());
            try (ResultSet rs = call.executeQuery()) {
                while (rs.next()) {
                    resultados.add(mapearLibro(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar Libros por título: " + e.getMessage());
        }
        return resultados;
    }

    @Override
    public List<Libro> buscarPorAutor(String autor) {
        List<Libro> resultados = new ArrayList<>();
        if (autor == null || autor.trim().isEmpty()) {
            return ListarTodos();
        }
        String sql = "{call sp_buscar_libros_por_autor(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement call = conexion.prepareCall(sql)) {
            call.setString(1, autor.trim());
            try (ResultSet rs = call.executeQuery()) {
                while (rs.next()) {
                    resultados.add(mapearLibro(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar Libros por autor: " + e.getMessage());
        }
        return resultados;
    }

    @Override
    public boolean actualizar(Libro libro) {
        String consulta = "{call sp_actualizarlibro(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement consultaCall = conexion.prepareCall(consulta)) {
            consultaCall.setString(1, libro.getIsbn());
            consultaCall.setString(2, libro.getTitulo());
            consultaCall.setDate(3, Date.valueOf(libro.getFechaPublicacion()));
            consultaCall.setDouble(4, libro.getPrecio());
            consultaCall.setInt(5, libro.getIdCategoria());
            consultaCall.setString(6, libro.getNitEditorial());
            consultaCall.setInt(7, libro.getStock());
            consultaCall.setInt(8, libro.getStockMinimo());
            consultaCall.setBoolean(9, libro.isActivo());
            return consultaCall.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar Libro: " + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            System.err.println("Fecha de publicación inválida, use el formato AAAA-MM-DD: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(String isbn) {
        String consultaSQL = "{call sp_eliminarlibro(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement consultaCall = conexion.prepareCall(consultaSQL)) {
            consultaCall.setString(1, isbn);
            return consultaCall.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar Libro: " + e.getMessage());
            return false;
        }
    }
}