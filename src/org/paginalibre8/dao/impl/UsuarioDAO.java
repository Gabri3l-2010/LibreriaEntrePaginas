/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.paginalibre8.dao.impl;

import org.paginalibre8.model.Usuario;
import org.paginalibre8.util.Conexion;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.CallableStatement;


//Data Acces Object, Objeto de acceso a los datos --> MYSQl usando Conexion
public class UsuarioDAO {    
    //inicioSesion
    public Usuario iniciarSesion(String usernarme, String passwordHash){                
        Usuario usuario = null;
        String sql = "{call sp_iniciar_sesion(?,?)}";
        
        //try-with-resources -- al final el try, los recursos se cierran auto
        //recursos: Connection, ResulSet
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql)){
            
            consulta.setString(1, usernarme);
            consulta.setString(2, passwordHash);
            
            try(ResultSet tablaResultado = consulta.executeQuery()){
                //comprobar que haya algo en el resultado
                if (tablaResultado.next()) {
                    //verdadero hay algo(datos) Mapear
                    usuario = new Usuario();
                    usuario.setId(tablaResultado.getInt(1));
                    usuario.setUsername(tablaResultado.getString(2));
                    usuario.setRol(tablaResultado.getString(3));
                }
            }            
        } catch (SQLException e) {
            System.err.println("Error en Iniciar Sesion: " + e.getMessage());                   
        }
        
        return usuario;
    }
    
    //registrarUsuario
    public boolean registrarUsuario(String username, String passwordHash){
        String sql = "{call sp_registrar_usuario(?,?,'Empleado')}";
        try (Connection conexion = Conexion.getInstancia().conectar();
                CallableStatement consulta = conexion.prepareCall(sql)){
            consulta.setString(1, username);
            consulta.setString(2, passwordHash);
            int filasAfectadas = consulta.executeUpdate();
            return filasAfectadas > 0;//simplificación del if _ true
        } catch (SQLException e) {
            System.err.println("Error en Registrar Usuario: " + e.getMessage());
            return false;
        }
        
    }
    
}