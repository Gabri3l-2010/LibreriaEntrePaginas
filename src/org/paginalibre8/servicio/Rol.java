/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.paginalibre8.servicio;


import java.util.Set;

public enum Rol {

    ADMIN(Set.of("GESTIONAR_USUARIOS", "GESTIONAR_INVENTARIO", "VENDER", "VER_REPORTES")),
    CAJERO(Set.of("VENDER")),
    BODEGA(Set.of("GESTIONAR_INVENTARIO"));

    private final Set<String> permisos;

    Rol(Set<String> permisos) {
        this.permisos = permisos;
    }

    public boolean tienePermiso(String permiso) {
        return permisos.contains(permiso);
    }

    /**
     * Convierte el texto guardado en la base de datos (ej. "Administrador",
     * "Empleado") al enum Rol correspondiente.
     */
    public static Rol fromString(String texto) {
        if (texto == null) {
            return null;
        }
        switch (texto.trim().toLowerCase()) {
            case "administrador":
            case "admin":
                return ADMIN;
            case "cajero":
                return CAJERO;
            case "bodega":
                return BODEGA;
            case "empleado":
                return CAJERO; // valor por defecto para autoregistro
            default:
                return null;
        }
    }
}