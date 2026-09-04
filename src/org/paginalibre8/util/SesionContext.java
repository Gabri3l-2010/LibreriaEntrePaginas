/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.paginalibre8.util;


import org.paginalibre8.model.Usuario;

public class SesionContext {
    private static SesionContext instancia;
    private Usuario usuairoActual;

    public SesionContext() {
    }
    
    public static synchronized SesionContext getInstancia(){
        if (instancia == null) {
            instancia = new SesionContext();
        }
        return instancia;
    }

    public Usuario getUsuairoActual() {
        return usuairoActual;
    }

    public void setUsuairoActual(Usuario usuairoActual) {
        this.usuairoActual = usuairoActual;
    }
    
    public void cerrarSesion(){
        this.usuairoActual = null;
    }
    
}  