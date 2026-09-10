package org.paginalibre8.dao.impl;

import java.util.List;
import org.paginalibre8.model.Usuario;

public interface UsuarioDAO {
    Usuario iniciarSesion(String username, String passwordHash);
    Usuario buscarPorUsername(String username);
    Usuario obtenerUsuarioPorId(int idUsuario);
    List<Usuario> listarUsuarios();
    List<Usuario> listarTodosUsuarios();
    boolean registrarUsuario(String username, String passwordHash, String rol, String nombre, String apellido, String correo);
    boolean registrarUsuario(String username, String passwordHash);
    boolean crearUsuario(Usuario usuario);
    boolean actualizarUsuario(Usuario usuario);
    boolean desactivarUsuario(int idUsuario);
    boolean eliminarUsuario(int idUsuario);
    boolean existeUsername(String username);
    boolean validarPasswordActual(int idUsuario, String passwordActualHash);
    boolean cambiarPassword(int idUsuario, String nuevaPasswordHash);
}
