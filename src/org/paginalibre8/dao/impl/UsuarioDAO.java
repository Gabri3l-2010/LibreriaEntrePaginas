package org.paginalibre8.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.paginalibre8.model.Usuario;
import org.paginalibre8.util.Conexion;

public class UsuarioDAO {


    public Usuario iniciarSesion(String username, String passwordHash) {
        Usuario usuario = null;
        String sql = "{call sp_iniciar_sesion(?, ?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement consultaCall = conexion.prepareCall(sql)) {
            consultaCall.setString(1, username);
            consultaCall.setString(2, passwordHash);
            try (ResultSet rs = consultaCall.executeQuery()) {
                if (rs.next()) {
                    usuario = mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("No es posible iniciar sesion intentelo de nuevo: " + e.getMessage());
        }
        return usuario;
    }

    public Usuario buscarPorUsername(String username) {
        Usuario usuario = null;
        String sql = "{call sp_buscar_usuario(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement consultaCall = conexion.prepareCall(sql)) {
            consultaCall.setString(1, username);
            try (ResultSet rs = consultaCall.executeQuery()) {
                if (rs.next()) {
                    usuario = mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            String sqlFallback = "SELECT id, username, rol, nombre, apellido, correo, activo FROM usuarios WHERE username = ?";
            try (Connection conexion = Conexion.getInstancia().conectar();
                 PreparedStatement ps = conexion.prepareStatement(sqlFallback)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        usuario = mapearUsuario(rs);
                    }
                }
            } catch (SQLException e2) {
                System.err.println("Error al buscar usuario: " + e2.getMessage());
            }
        }
        return usuario;
    }

    /** Lista todos los usuarios para la vista JavaFX de gestión. */
    public List<Usuario> listarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id, username, rol, nombre, apellido, correo, activo "
                   + "FROM usuarios ORDER BY id";
        try (Connection conexion = Conexion.getInstancia().conectar();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        }
        return usuarios;
    }

    /** Registro completo: usado por el alta de usuarios desde administración. */
    public boolean registrarUsuario(String username, String passwordHash, String rol,
                                    String nombre, String apellido, String correo) {
        String sql6 = "{call sp_registrar_usuario(?, ?, ?, ?, ?, ?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement call = conexion.prepareCall(sql6)) {
            call.setString(1, username);
            call.setString(2, passwordHash);
            call.setString(3, rol);
            call.setString(4, nombre);
            call.setString(5, apellido);
            call.setString(6, correo);
            call.execute();
            return true;
        } catch (SQLException e) {
            // Intentar con procedimiento de 3 parámetros (versión heredada)
            String sql3 = "{call sp_registrar_usuario(?, ?, ?)}";
            try (Connection conexion = Conexion.getInstancia().conectar();
                 CallableStatement call = conexion.prepareCall(sql3)) {
                call.setString(1, username);
                call.setString(2, passwordHash);
                call.setString(3, rol);
                call.execute();
                return true;
            } catch (SQLException e2) {
                // Fallback a sentencia INSERT directa
                String sqlInsert = "INSERT INTO usuarios (username, password_hash, rol, nombre, apellido, correo) VALUES (?, ?, ?, ?, ?, ?)";
                try (Connection conexion = Conexion.getInstancia().conectar();
                     PreparedStatement ps = conexion.prepareStatement(sqlInsert)) {
                    ps.setString(1, username);
                    ps.setString(2, passwordHash);
                    ps.setString(3, rol);
                    ps.setString(4, nombre);
                    ps.setString(5, apellido);
                    ps.setString(6, correo);
                    return ps.executeUpdate() > 0;
                } catch (SQLException e3) {
                    System.err.println("Error al registrar usuario: " + e3.getMessage());
                    return false;
                }
            }
        }
    }


    /** Sobrecarga para autoregistro simple: usuario y contraseña únicamente.
     *  Asigna rol "Empleado" por defecto y deja nombre/apellido/correo vacíos. */
    public boolean registrarUsuario(String username, String passwordHash) {
        return registrarUsuario(username, passwordHash, "Empleado", "", "", "");
    }

    /** Desactivación lógica: el registro permanece en la base de datos. */
    public boolean desactivarUsuario(int id) {
        String sql = "UPDATE usuarios SET activo = 0 WHERE id = ?";
        try (Connection conexion = Conexion.getInstancia().conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al desactivar usuario: " + e.getMessage());
            return false;
        }
    }

    /** Actualización de los datos de un usuario existente (T1.13). */
    public boolean actualizarUsuario(Usuario usuario) {
        String sql = "UPDATE usuarios SET username = ?, rol = ?, nombre = ?, apellido = ?, correo = ?, activo = ? WHERE id = ?";
        try (Connection conexion = Conexion.getInstancia().conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, usuario.getUsername());
            ps.setString(2, usuario.getRol());
            ps.setString(3, usuario.getNombre());
            ps.setString(4, usuario.getApellido());
            ps.setString(5, usuario.getCorreo());
            ps.setBoolean(6, usuario.isActivo());
            ps.setInt(7, usuario.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }


    public boolean existeUsername(String username) {
        String sql = "SELECT 1 FROM usuarios WHERE username = ? LIMIT 1";
        try (Connection conexion = Conexion.getInstancia().conectar();
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error al validar username: " + e.getMessage());
            return false;
        }
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("id"));
        usuario.setUsername(rs.getString("username"));
        usuario.setRol(rs.getString("rol"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setApellido(rs.getString("apellido"));
        try {
            usuario.setCorreo(rs.getString("correo"));
        } catch (SQLException ignored) {
            // Permite seguir funcionando si el procedimiento de login no devuelve correo.
        }
        try {
            usuario.setActivo(rs.getBoolean("activo"));
        } catch (SQLException ignored) {
            usuario.setActivo(true);
        }
        return usuario;
    }
}