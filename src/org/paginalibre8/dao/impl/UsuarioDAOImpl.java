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
            String sqlFallback = "SELECT id_usuario AS id, id_usuario, username, password_hash, rol, nombre, apellido, correo, activo FROM usuarios WHERE LOWER(username) = LOWER(?) AND password_hash = ? AND activo = 1 LIMIT 1";
            try (Connection conexion = Conexion.getInstancia().conectar();
                 PreparedStatement ps = conexion.prepareStatement(sqlFallback)) {
                ps.setString(1, username);
                ps.setString(2, passwordHash);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        usuario = mapearUsuario(rs);
                    }
                }
            } catch (SQLException e2) {
                System.err.println("No es posible iniciar sesion intentelo de nuevo: " + e2.getMessage());
            }
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
            String sqlFallback = "SELECT id_usuario AS id, username, rol, nombre, apellido, correo, activo FROM usuarios WHERE username = ?";
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
            String sqlFallback = "SELECT * FROM usuarios";
            try (Connection conexion = Conexion.getInstancia().conectar();
                 PreparedStatement ps = conexion.prepareStatement(sqlFallback);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(mapearUsuario(rs));
                }
            } catch (SQLException e2) {
                System.err.println("Error al listar usuarios: " + e2.getMessage());
            }
        }
        return usuarios;
    }

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
            String sql3 = "{call sp_registrar_usuario(?, ?, ?)}";
            try (Connection conexion = Conexion.getInstancia().conectar();
                 CallableStatement call = conexion.prepareCall(sql3)) {
                call.setString(1, username);
                call.setString(2, passwordHash);
                call.setString(3, rol);
                call.execute();
                return true;
            } catch (SQLException e2) {
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

   
    public boolean registrarUsuario(String username, String passwordHash) {
        return registrarUsuario(username, passwordHash, "cajero", "", "", "");
    }

    public boolean desactivarUsuario(int id) {
        String sql = "{call sp_desactivar_usuario(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement call = conexion.prepareCall(sql)) {
            call.setInt(1, id);
            return call.executeUpdate() > 0;
        } catch (SQLException e) {
            String sqlFallback = "UPDATE usuarios SET activo = 0 WHERE id_usuario = ?";
            try (Connection conexion = Conexion.getInstancia().conectar();
                 PreparedStatement ps = conexion.prepareStatement(sqlFallback)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            } catch (SQLException e2) {
                System.err.println("Error al desactivar usuario: " + e2.getMessage());
                return false;
            }
        }
    }

    /** Actualización de los datos de un usuario existente (T1.13). */
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
            String sqlFallback = "UPDATE usuarios SET username = ?, rol = ?, nombre = ?, apellido = ?, correo = ?, activo = ? WHERE id_usuario = ?";
            try (Connection conexion = Conexion.getInstancia().conectar();
                 PreparedStatement ps = conexion.prepareStatement(sqlFallback)) {
                ps.setString(1, usuario.getUsername());
                ps.setString(2, usuario.getRol());
                ps.setString(3, usuario.getNombre());
                ps.setString(4, usuario.getApellido());
                ps.setString(5, usuario.getCorreo());
                ps.setBoolean(6, usuario.isActivo());
                ps.setInt(7, usuario.getId());
                return ps.executeUpdate() > 0;
            } catch (SQLException e2) {
                System.err.println("Error al actualizar usuario: " + e2.getMessage());
                return false;
            }
        }
    }

    public boolean existeUsername(String username) {
        String sql = "{call sp_existe_username(?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement call = conexion.prepareCall(sql)) {
            call.setString(1, username);
            try (ResultSet rs = call.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            String sqlFallback = "SELECT 1 FROM usuarios WHERE username = ? LIMIT 1";
            try (Connection conexion = Conexion.getInstancia().conectar();
                 PreparedStatement ps = conexion.prepareStatement(sqlFallback)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            } catch (SQLException e2) {
                System.err.println("Error al validar username: " + e2.getMessage());
                return false;
            }
        }
    }

    
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
            String sqlFallback = "SELECT 1 FROM usuarios WHERE id_usuario = ? AND password_hash = ? LIMIT 1";
            try (Connection conexion = Conexion.getInstancia().conectar();
                 PreparedStatement ps = conexion.prepareStatement(sqlFallback)) {
                ps.setInt(1, idUsuario);
                ps.setString(2, passwordActualHash);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            } catch (SQLException e2) {
                System.err.println("Error al validar contraseña actual: " + e2.getMessage());
                return false;
            }
        }
    }

    
    public boolean cambiarPassword(int idUsuario, String nuevaPasswordHash) {
        String sqlProc = "{call sp_cambiar_password(?, ?)}";
        try (Connection conexion = Conexion.getInstancia().conectar();
             CallableStatement call = conexion.prepareCall(sqlProc)) {
            call.setInt(1, idUsuario);
            call.setString(2, nuevaPasswordHash);
            call.execute();
            return true;
        } catch (SQLException e) {
            String sqlFallback = "UPDATE usuarios SET password_hash = ? WHERE id_usuario = ?";
            try (Connection conexion = Conexion.getInstancia().conectar();
                 PreparedStatement ps = conexion.prepareStatement(sqlFallback)) {
                ps.setString(1, nuevaPasswordHash);
                ps.setInt(2, idUsuario);
                return ps.executeUpdate() > 0;
            } catch (SQLException e2) {
                String sqlFallback2 = "UPDATE usuarios SET password_hash = ? WHERE id = ?";
                try (Connection conexion = Conexion.getInstancia().conectar();
                     PreparedStatement ps = conexion.prepareStatement(sqlFallback2)) {
                    ps.setString(1, nuevaPasswordHash);
                    ps.setInt(2, idUsuario);
                    return ps.executeUpdate() > 0;
                } catch (SQLException e3) {
                    System.err.println("Error al cambiar contraseña: " + e3.getMessage());
                    return false;
                }
            }
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
            String sqlFallback = "SELECT * FROM usuarios WHERE id_usuario = ?";
            try (Connection conexion = Conexion.getInstancia().conectar();
                 PreparedStatement ps = conexion.prepareStatement(sqlFallback)) {
                ps.setInt(1, idUsuario);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapearUsuario(rs);
                    }
                }
            } catch (SQLException e2) {
                System.err.println("Error al obtener usuario por ID: " + e2.getMessage());
            }
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