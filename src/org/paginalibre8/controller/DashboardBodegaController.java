package org.paginalibre8.controller;

import java.net.URL;
import java.util.ResourceBundle;
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

public class DashboardBodegaController implements Initializable, DashboardController {

    @FXML private Label lblUsuario;
    private Usuario usuarioActual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (SesionUsuario.getInstancia().haySesionActiva()) {
            if (!SesionUsuario.getInstancia().tienePermiso("GESTIONAR_INVENTARIO")) {
                mostrarAdvertencia("Acceso Denegado", "No cuentas con permisos para acceder al Panel de Bodega.");
                return;
            }
            this.usuarioActual = SesionUsuario.getInstancia().getUsuarioActual();
            if (lblUsuario != null) {
                lblUsuario.setText("Bodega: " + SesionUsuario.getInstancia().getNombreCompleto());
            }
        }
    }

    @Override
    public void iniciarUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
        if (lblUsuario != null && usuario != null) {
            lblUsuario.setText("Bodega: " + usuario.getUsername());
        }
    }

    @FXML
    private void handleInventario(ActionEvent event) {
        if (!SesionUsuario.getInstancia().tienePermiso("GESTIONAR_INVENTARIO")) {
            mostrarAdvertencia("Acceso Denegado", "No cuentas con permiso para gestionar el inventario.");
            return;
        }
        mostrarInfo("Módulo de Inventario", "Abriendo catálogo e inventario...");
    }

    @FXML
    private void handleCategorias(ActionEvent event) {
        if (!SesionUsuario.getInstancia().tienePermiso("GESTIONAR_INVENTARIO")) {
            mostrarAdvertencia("Acceso Denegado", "No cuentas con permiso para gestionar categorías.");
            return;
        }
        mostrarInfo("Módulo de Categorías", "Abriendo categorías y clasificación de libros...");
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
    private void handleSalir(ActionEvent event) {
        SesionUsuario.getInstancia().cerrarSesion();
        try {
            Main.cambiarEscena("/org/paginalibre8/view/style/InicioSesionView.fxml");
        } catch (Exception e) {
            mostrarError("Error al cerrar sesión: " + e.getMessage());
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
