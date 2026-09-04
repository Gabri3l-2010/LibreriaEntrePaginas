package org.paginalibre8.servicio;

import org.paginalibre8.model.Usuario;

/**
 * Autor: Gabriel Chiu
 */
public final class SesionUsuario {

    private static SesionUsuario instancia;

    private Usuario usuarioActual;
    private Rol rolActual;

    private SesionUsuario() {
    }

    public static synchronized SesionUsuario getInstancia() {
        if (instancia == null) {
            instancia = new SesionUsuario();
        }
        return instancia;
    }

    /**
     * Inicia la sesión con el usuario autenticado.
     */
    public void iniciarSesion(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        this.usuarioActual = usuario;
        this.rolActual = Rol.fromString(usuario.getRol());
    }

    /**
     * Cierra la sesión actual.
     */
    public void cerrarSesion() {
        this.usuarioActual = null;
        this.rolActual = null;
    }

    public boolean haySesionActiva() {
        return usuarioActual != null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public Rol getRolActual() {
        return rolActual;
    }

    public String getNombreCompleto() {
        if (usuarioActual == null) return "";
        String n = usuarioActual.getNombre() != null ? usuarioActual.getNombre() : "";
        String a = usuarioActual.getApellido() != null ? usuarioActual.getApellido() : "";
        return (n + " " + a).trim();
    }

    public boolean tienePermiso(String permiso) {
        if (rolActual == null) return false;
        return rolActual.tienePermiso(permiso);
    }

    public boolean esAdmin() {
        return rolActual == Rol.ADMIN;
    }

    public boolean esCajero() {
        return rolActual == Rol.CAJERO;
    }

    public boolean esBodega() {
        return rolActual == Rol.BODEGA;
    }
}