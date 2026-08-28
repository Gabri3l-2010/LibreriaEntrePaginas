package org.paginalibre8.controller;

import java.io.IOException;
import org.paginalibre8.dao.impl.UsuarioDAO;
import org.paginalibre8.util.SecurityUtil;
import org.paginalibre8.model.Usuario;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.paginalibre8.system.Main;
import org.paginalibre8.util.SesionContext;
import org.paginalibre8.util.ValidacionException;

public class InisioSesionController implements Initializable {
    
    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Button btnIniciarSesion;
    @FXML
    private Label lblMensaje;
    
    private UsuarioDAO usuarioDAO;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        usuarioDAO = new UsuarioDAO();
        lblMensaje.setText("");
    }
    
    @FXML
    public void eventoInicioSesion(ActionEvent evento) {
        try {
            ValidacionException.validarNoVacio(txtUsuario.getText(), "usuario");
            ValidacionException.validarNoVacio(txtPassword.getText(), "contraseña");
            
            String usuario = txtUsuario.getText();
            String password = txtPassword.getText();
            String passwordHash = SecurityUtil.hashSHA256(password);
            Usuario usuarioIniciado = usuarioDAO.iniciarSesion(usuario, passwordHash);            
            
            if (usuarioIniciado != null) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Inicio correcto");
                abrirDashboard(usuarioIniciado);
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Usuario o contraseña incorrectos");
            }
            
        } catch (ValidacionException e) {
            mostrarAlerta(Alert.AlertType.WARNING, e.getMessage());
            lblMensaje.setText(e.getMessage());
        }        
    }

    public void eventoRegistrarse(ActionEvent evento){
        try {
            Main.cambiarEscena("/org/paginalibre8/view/style/RegistrarUsuarioView.fxml");
        } catch (IOException e) {
            System.err.println("Error al cargar registro: " + e.getMessage());
            lblMensaje.setText("Error interno");
        }
    }
    
    private void abrirDashboard(Usuario usuario) {
        SesionContext.getInstancia().setUsuairoActual(usuario);
        String rutaFXML = "";        
        
        switch (usuario.getRol().toLowerCase()) {
            case "admin":
                rutaFXML = "/org/ar/view/AdminDashboardView.fxml";
                break;
            case "cajero":
                rutaFXML = "/org/ar/view/CajeroDashboardView.fxml";
                break;
            case "empleado":
                rutaFXML = "/org/ar/view/EmpleadoDashboardView.fxml";
                break;
            default:
                System.err.println("Rol no reconocido: " + usuario.getRol());
                lblMensaje.setText("Rol no asignado o inválido");
                return;
        }

        try {
            Main.cambiarEscena(rutaFXML);
        } catch (IOException e) {
            System.err.println("Error al cargar la vista: " + rutaFXML + " -> " + e.getMessage());
            lblMensaje.setText("Error interno");
        }
    }
    
    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alerta = new Alert(tipo, mensaje, ButtonType.OK);
        alerta.show();
    }
}