package org.paginalibre8.model;

import javafx.beans.property.*;

public class Usuario {
    private IntegerProperty id;
    private StringProperty username;
    private StringProperty rol;
    private StringProperty nombre;
    private StringProperty apellido;
    private StringProperty correo;
    private BooleanProperty activo;

    public Usuario() {
        this.id = new SimpleIntegerProperty();
        this.username = new SimpleStringProperty();
        this.rol = new SimpleStringProperty();
        this.nombre = new SimpleStringProperty();
        this.apellido = new SimpleStringProperty();
        this.correo = new SimpleStringProperty();
        this.activo = new SimpleBooleanProperty();
    }

    public Usuario(int id, String username, String rol, String nombre, String apellido, String correo, boolean activo) {
        this.id = new SimpleIntegerProperty(id);
        this.username = new SimpleStringProperty(username);
        this.rol = new SimpleStringProperty(rol);
        this.nombre = new SimpleStringProperty(nombre);
        this.apellido = new SimpleStringProperty(apellido);
        this.correo = new SimpleStringProperty(correo);
        this.activo = new SimpleBooleanProperty(activo);
    }

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public String getUsername() { return username.get(); }
    public void setUsername(String username) { this.username.set(username); }
    public StringProperty usernameProperty() { return username; }

    public String getRol() { return rol.get(); }
    public void setRol(String rol) { this.rol.set(rol); }
    public StringProperty rolProperty() { return rol; }

    public String getNombre() { return nombre.get(); }
    public void setNombre(String nombre) { this.nombre.set(nombre); }
    public StringProperty nombreProperty() { return nombre; }

    public String getApellido() { return apellido.get(); }
    public void setApellido(String apellido) { this.apellido.set(apellido); }
    public StringProperty apellidoProperty() { return apellido; }

    public String getCorreo() { return correo.get(); }
    public void setCorreo(String correo) { this.correo.set(correo); }
    public StringProperty correoProperty() { return correo; }

    public boolean isActivo() { return activo.get(); }
    public void setActivo(boolean activo) { this.activo.set(activo); }
    public BooleanProperty activoProperty() { return activo; }
}