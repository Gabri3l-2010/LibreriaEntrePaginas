/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.paginalibre8.dao.impl;


import java.util.ArrayList;
import org.paginalibre8.model.Usuario;

 interface UsuarioDAO {
    public Usuario iniciarSesion(String usernarme, String passwordHash);
    public boolean crearUsuario(Usuario usuario);
    public boolean actualizarUsuario(Usuario usuario);
    public boolean cambiarPassword(int idUsuario, String passwordHash);
    public boolean desactivarUsuario(int idUsuario);
    public boolean eliminarUsuario(int idUsuario);
    public ArrayList<Usuario> listarTodosUsuarios();
    public Usuario obtenerUsuarioPorId(int idUsuario);
}
