package org.paginalibre8.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.paginalibre8.model.Usuario;
import org.paginalibre8.servicio.SesionUsuario;
import org.paginalibre8.system.Main;

public class MenuPrincipalDashboardController implements Initializable, DashboardController {

    @FXML private Label lblUsuario;
    private Usuario usuarioActual;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (SesionUsuario.getInstancia().haySesionActiva()) {
            this.usuarioActual = SesionUsuario.getInstancia().getUsuarioActual();
            if (lblUsuario != null) {
                lblUsuario.setText("Administrador: " + SesionUsuario.getInstancia().getNombreCompleto());
            }
        }
    }    
    
    @Override
    public void iniciarUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
        if (lblUsuario != null && usuario != null) {
            lblUsuario.setText("Administrador: " + usuario.getUsername());
        }
    }
    
    @FXML
    private void handleCategorias() {
        mostrarInfo("Módulo de Categorías", "Abriendo gestión de categorías...");
    }

    @FXML
    private void handleEditoriales() {
        mostrarInfo("Módulo de Editoriales", "Abriendo gestión de editoriales...");
    }
    
    @FXML
    private void handleClientes() {
        mostrarInfo("Módulo de Clientes", "Abriendo gestión de clientes...");
    }

    @FXML
    private void handleAutores() {
        mostrarInfo("Módulo de Autores", "Abriendo gestión de autores...");
    }

    @FXML
    private void handleUsuarios() {
        if (!SesionUsuario.getInstancia().esAdmin()) {
            mostrarAdvertencia("Acceso Denegado", "No cuentas con permisos suficientes para acceder a la Gestión de Usuarios.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/paginalibre8/view/style/Usuarios.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Gestión de Usuarios - Librería Entre Páginas");
            stage.setScene(new Scene(root, 1050, 550));
            stage.show();
        } catch (Exception e) {
            mostrarError("Error al cargar la vista de gestión de usuarios:\n" + e.getMessage());
        }
    }

    @FXML
    private void handleCambiarPassword(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/paginalibre8/view/style/CambioPasswordView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Cambio de Contraseña");
            stage.setScene(new Scene(root, 420, 340));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            mostrarError("Error al abrir cambio de contraseña: " + e.getMessage());
        }
    }

    @FXML
    private void handleSalir() {
        SesionUsuario.getInstancia().cerrarSesion();
        try {
            Main.cambiarEscena("/org/paginalibre8/view/style/InicioSesionView.fxml");
        } catch (Exception e) {
            Platform.exit();
        }
    }

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAdvertencia(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}