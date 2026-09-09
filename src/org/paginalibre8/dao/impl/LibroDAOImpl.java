package org.paginalibre8.dao.impl;

import org.paginalibre8.dao.impl.LibroDAO;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;
import org.paginalibre8.model.Libro;
import org.paginalibre8.util.Conexion;

public class LibroDAOImpl implements LibroDAO {

    @Override
    public List<Libro> ListarTodos() {
        List<Libro> libros = new ArrayList();
        String consulta = "{call sp_listarlibros()}";
        try (Connection conexion = Conexion.getInstancia().conectar(); CallableStatement consultaCall = conexion.prepareCall(consulta); ResultSet tablaResultado = consultaCall.executeQuery();) {
            while (tablaResultado.next()) {
                libros.add(new Libro(
                        tablaResultado.getString("isbn"),
                        tablaResultado.getString("titulo"),
                        tablaResultado.getDate("fecha_publicacion") != null
                                ? tablaResultado.getDate("fecha_publicacion").toString() : "",
                        tablaResultado.getDouble("precio"),
                        tablaResultado.getInt("id_categoria"),
                        tablaResultado.getString("nit_editorial")
                ));
            }
        } catch (SQLException e) {
            System.err.print("Error al listar Libros:" + e.getMessage());
        }
        return libros;
    }

    @Override
    public boolean crear(Libro libro) {
        String consulta = "{call sp_insertarlibro(?, ?, ?, ?, ?, ?)}";
        try (Connection conexion = Conexion.getInstancia().conectar(); CallableStatement consultaCall = conexion.prepareCall(consulta)) {
            consultaCall.setString(1, libro.getIsbn());
            consultaCall.setString(2, libro.getTitulo());
            consultaCall.setDate(3, Date.valueOf(libro.getFechaPublicacion()));
            consultaCall.setDouble(4, libro.getPrecio());
            consultaCall.setInt(5, libro.getIdCategoria());
            consultaCall.setString(6, libro.getNitEditorial());
            return consultaCall.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.print("Error al crear Libro: " + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            System.err.print("Fecha de publicación inválida, use el formato AAAA-MM-DD: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Libro buscarPorId(String isbn) {
        Libro libro = new Libro();
        String consultaSQL = "{call sp_buscarlibro(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar(); CallableStatement consultaCall = conexion.prepareCall(consultaSQL);) {
            consultaCall.setString(1, isbn);
            ResultSet tablaResultado = consultaCall.executeQuery();
            if (tablaResultado.next()) {
                libro.setIsbn(tablaResultado.getString("isbn"));
                libro.setTitulo(tablaResultado.getString("titulo"));
                libro.setFechaPublicacion(tablaResultado.getDate("fecha_publicacion") != null
                        ? tablaResultado.getDate("fecha_publicacion").toString() : "");
                libro.setPrecio(tablaResultado.getDouble("precio"));
                libro.setIdCategoria(tablaResultado.getInt("id_categoria"));
                libro.setNitEditorial(tablaResultado.getString("nit_editorial"));
            } else {
                return null;
            }
        } catch (SQLException e) {
            System.err.print("Error al buscar Libro:" + e.getMessage());
        }
        return libro;
    }

    @Override
    public boolean actualizar(Libro libro) {
        String consulta = "{call sp_actualizarlibro(?, ?, ?, ?, ?, ?)}";
        try (Connection conexion = Conexion.getInstancia().conectar(); CallableStatement consultaCall = conexion.prepareCall(consulta)) {
            consultaCall.setString(1, libro.getIsbn());
            consultaCall.setString(2, libro.getTitulo());
            consultaCall.setDate(3, Date.valueOf(libro.getFechaPublicacion()));
            consultaCall.setDouble(4, libro.getPrecio());
            consultaCall.setInt(5, libro.getIdCategoria());
            consultaCall.setString(6, libro.getNitEditorial());
            return consultaCall.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.print("Error al actualizar Libro: " + e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            System.err.print("Fecha de publicación inválida, use el formato AAAA-MM-DD: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean eliminar(String isbn) {
        String consultaSQL = "{call sp_eliminarlibro(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar(); CallableStatement consultaCall = conexion.prepareCall(consultaSQL)) {
            consultaCall.setString(1, isbn);
            int filasAfectadas = consultaCall.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Libro eliminado con exito");
                return true;
            } else {
                System.out.println("No se encontro ningun libro con ese isbn");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Error al eliminar libro" + e.getMessage());
            return false;
        }
    }

    
    public boolean crear(Object libro) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    
    public boolean actualizar(Object libro) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}