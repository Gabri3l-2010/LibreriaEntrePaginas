package org.paginalibre8.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.paginalibre8.model.Libro;
import org.paginalibre8.util.Conexion;

public class LibroDAOImpl implements LibroDAO {

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
            // Fallback SQL directo en caso de que el procedimiento almacenado falle
            String sqlFallback = "SELECT * FROM libros";
            try (Connection conexion = Conexion.getInstancia().conectar();
                 PreparedStatement ps = conexion.prepareStatement(sqlFallback);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    libros.add(mapearLibro(rs));
                }
            } catch (SQLException e2) {
                System.err.println("Error al listar Libros: " + e2.getMessage());
            }
        }
        return libros;
    }

    @Override
    public boolean crear(Libro libro) {
        String consulta = "{call sp_insertarlibro(?, ?, ?, ?, ?, ?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement consultaCall = conexion.prepareCall(consulta)) {
            consultaCall.setString(1, libro.getIsbn());
            consultaCall.setString(2, libro.getTitulo());
            consultaCall.setDate(3, Date.valueOf(libro.getFechaPublicacion()));
            consultaCall.setDouble(4, libro.getPrecio());
            consultaCall.setInt(5, libro.getIdCategoria());
            consultaCall.setString(6, libro.getNitEditorial());
            return consultaCall.executeUpdate() > 0;
        } catch (SQLException e) {
            String sqlInsert = "INSERT INTO libros (isbn, titulo, fecha_publicacion, precio, stock_actual, id_categoria, nit_editorial) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (Connection conexion = Conexion.getInstancia().conectar();
                 PreparedStatement ps = conexion.prepareStatement(sqlInsert)) {
                ps.setString(1, libro.getIsbn());
                ps.setString(2, libro.getTitulo());
                ps.setDate(3, Date.valueOf(libro.getFechaPublicacion()));
                ps.setDouble(4, libro.getPrecio());
                ps.setInt(5, libro.getStock());
                ps.setInt(6, libro.getIdCategoria());
                ps.setString(7, libro.getNitEditorial());
                return ps.executeUpdate() > 0;
            } catch (SQLException e2) {
                System.err.println("Error al crear Libro: " + e2.getMessage());
                return false;
            }
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
            ResultSet tablaResultado = consultaCall.executeQuery();
            if (tablaResultado.next()) {
                return mapearLibro(tablaResultado);
            }
        } catch (SQLException e) {
            // Fallback SQL directo por ISBN
        }
        String sqlFallback = "SELECT * FROM libros WHERE isbn = ? OR isbn LIKE ? LIMIT 1";
        try (Connection conexion = Conexion.getInstancia().conectar();
             PreparedStatement ps = conexion.prepareStatement(sqlFallback)) {
            ps.setString(1, isbn.trim());
            ps.setString(2, "%" + isbn.trim() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearLibro(rs);
                }
            }
        } catch (SQLException e2) {
            System.err.println("Error al buscar Libro por ISBN: " + e2.getMessage());
        }
        return null;
    }

  
    @Override
    public List<Libro> buscarPorTitulo(String titulo) {
        List<Libro> resultados = new ArrayList<>();
        if (titulo == null || titulo.trim().isEmpty()) {
            return ListarTodos();
        }
        String sql = "SELECT * FROM libros WHERE LOWER(titulo) LIKE LOWER(?)";
        try (Connection conexion = Conexion.getInstancia().conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, "%" + titulo.trim() + "%");
            try (ResultSet rs = ps.executeQuery()) {
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
        String sql = "SELECT l.*, l.stock_actual AS stock, CONCAT(a.nombre_autor, ' ', a.apellido_autor) AS autor "
                   + "FROM libros l "
                   + "JOIN autores_libro al ON l.isbn = al.isbn "
                   + "JOIN autores a ON al.id_autor = a.id_autor "
                   + "WHERE LOWER(CONCAT(a.nombre_autor, ' ', a.apellido_autor)) LIKE LOWER(?)";
        try (Connection conexion = Conexion.getInstancia().conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, "%" + autor.trim() + "%");
            try (ResultSet rs = ps.executeQuery()) {
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
        String consulta = "{call sp_actualizarlibro(?, ?, ?, ?, ?, ?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement consultaCall = conexion.prepareCall(consulta)) {
            consultaCall.setString(1, libro.getIsbn());
            consultaCall.setString(2, libro.getTitulo());
            consultaCall.setDate(3, Date.valueOf(libro.getFechaPublicacion()));
            consultaCall.setDouble(4, libro.getPrecio());
            consultaCall.setInt(5, libro.getIdCategoria());
            consultaCall.setString(6, libro.getNitEditorial());
            return consultaCall.executeUpdate() > 0;
        } catch (SQLException e) {
            String sqlUpdate = "UPDATE libros SET titulo = ?, fecha_publicacion = ?, precio = ?, stock_actual = ?, id_categoria = ?, nit_editorial = ? WHERE isbn = ?";
            try (Connection conexion = Conexion.getInstancia().conectar();
                 PreparedStatement ps = conexion.prepareStatement(sqlUpdate)) {
                ps.setString(1, libro.getTitulo());
                ps.setDate(2, Date.valueOf(libro.getFechaPublicacion()));
                ps.setDouble(3, libro.getPrecio());
                ps.setInt(4, libro.getStock());
                ps.setInt(5, libro.getIdCategoria());
                ps.setString(6, libro.getNitEditorial());
                ps.setString(7, libro.getIsbn());
                return ps.executeUpdate() > 0;
            } catch (SQLException e2) {
                System.err.println("Error al actualizar Libro: " + e2.getMessage());
                return false;
            }
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
            String sqlFallback = "DELETE FROM libros WHERE isbn = ?";
            try (Connection conexion = Conexion.getInstancia().conectar();
                 PreparedStatement ps = conexion.prepareStatement(sqlFallback)) {
                ps.setString(1, isbn);
                return ps.executeUpdate() > 0;
            } catch (SQLException e2) {
                System.err.println("Error al eliminar Libro: " + e2.getMessage());
                return false;
            }
        }
    }
}