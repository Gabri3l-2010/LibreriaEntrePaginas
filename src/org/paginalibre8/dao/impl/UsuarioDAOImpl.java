package org.paginalibre8.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.paginalibre8.model.Usuario;
import org.paginalibre8.util.Conexion;

public class UsuarioDAOImpl implements UsuarioDAO {

    @Override
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
            System.err.println("Error al iniciar sesión: " + e.getMessage());
        }
        return usuario;
    }

    @Override
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
            System.err.println("Error al buscar usuario por username: " + e.getMessage());
        }
        return usuario;
    }

    @Override
    public List<Usuario> listarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "{call sp_listar_usuarios()}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement call = conexion.prepareCall(sql);
             ResultSet rs = call.executeQuery()) {
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        }
        return usuarios;
    }

    @Override
    public boolean registrarUsuario(String username, String passwordHash, String rol,
                                    String nombre, String apellido, String correo) {
        String sql = "{call sp_registrar_usuario(?, ?, ?, ?, ?, ?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement call = conexion.prepareCall(sql)) {
            call.setString(1, username);
            call.setString(2, passwordHash);
            call.setString(3, rol);
            call.setString(4, nombre);
            call.setString(5, apellido);
            call.setString(6, correo);
            call.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean registrarUsuario(String username, String passwordHash) {
        return registrarUsuario(username, passwordHash, "cajero", "", "", "");
    }

    @Override
    public boolean desactivarUsuario(int id) {
        String sql = "{call sp_desactivar_usuario(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement call = conexion.prepareCall(sql)) {
            call.setInt(1, id);
            return call.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al desactivar usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean actualizarUsuario(Usuario usuario) {
        String sql = "{call sp_actualizar_usuario(?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement call = conexion.prepareCall(sql)) {
            call.setInt(1, usuario.getId());
            call.setString(2, usuario.getUsername());
            call.setString(3, usuario.getRol());
            call.setString(4, usuario.getNombre());
            call.setString(5, usuario.getApellido());
            call.setString(6, usuario.getCorreo());
            call.setBoolean(7, usuario.isActivo());
            return call.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean existeUsername(String username) {
        String sql = "{call sp_existe_username(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement call = conexion.prepareCall(sql)) {
            call.setString(1, username);
            try (ResultSet rs = call.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error al validar username: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean validarPasswordActual(int idUsuario, String passwordActualHash) {
        String sql = "{call sp_validar_password(?, ?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement call = conexion.prepareCall(sql)) {
            call.setInt(1, idUsuario);
            call.setString(2, passwordActualHash);
            try (ResultSet rs = call.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error al validar contraseña actual: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean cambiarPassword(int idUsuario, String nuevaPasswordHash) {
        String sqlProc = "{call sp_cambiar_password(?, ?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement call = conexion.prepareCall(sqlProc)) {
            call.setInt(1, idUsuario);
            call.setString(2, nuevaPasswordHash);
            call.execute();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al cambiar contraseña: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean crearUsuario(Usuario usuario) {
        return registrarUsuario(usuario.getUsername(), "", usuario.getRol(), usuario.getNombre(), usuario.getApellido(), usuario.getCorreo());
    }

    @Override
    public boolean eliminarUsuario(int idUsuario) {
        return desactivarUsuario(idUsuario);
    }

    @Override
    public List<Usuario> listarTodosUsuarios() {
        return listarUsuarios();
    }

    @Override
    public Usuario obtenerUsuarioPorId(int idUsuario) {
        String sql = "{call sp_obtener_usuario_por_id(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement call = conexion.prepareCall(sql)) {
            call.setInt(1, idUsuario);
            try (ResultSet rs = call.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por ID: " + e.getMessage());
        }
        return null;
    }

    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        try {
            usuario.setId(rs.getInt("id"));
        } catch (SQLException ignored) {
            usuario.setId(rs.getInt("id_usuario"));
        }
        usuario.setUsername(rs.getString("username"));
        usuario.setRol(rs.getString("rol"));
        usuario.setNombre(rs.getString("nombre"));
        usuario.setApellido(rs.getString("apellido"));
        try {
            usuario.setCorreo(rs.getString("correo"));
        } catch (SQLException ignored) {
        }
        try {
            usuario.setActivo(rs.getBoolean("activo"));
        } catch (SQLException ignored) {
            usuario.setActivo(true);
        }
        return usuario;
    }
}