package org.paginalibre8.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.paginalibre8.model.Usuario;
import org.paginalibre8.servicio.SesionUsuario;
import org.paginalibre8.system.Main;

public class DashboardBodegaController implements Initializable, DashboardController {

    @FXML private Label lblUsuario;
    @FXML private StackPane contentArea;

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
        Platform.runLater(this::handleInventario);
    }

    @Override
    public void iniciarUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
        if (lblUsuario != null && usuario != null) {
            lblUsuario.setText("Bodega: " + usuario.getUsername());
        }
        Platform.runLater(this::handleInventario);
    }

    @FXML
    private void handleInventario() {
        if (!SesionUsuario.getInstancia().tienePermiso("GESTIONAR_INVENTARIO")) {
            mostrarAdvertencia("Acceso Denegado", "No cuentas con permiso para gestionar el inventario.");
            return;
        }
        cargarVista("/org/paginalibre8/view/style/RegistrarIngresoInventarioView.fxml");
    }

    @FXML
    private void handleSalidaInventario() {
        if (!SesionUsuario.getInstancia().tienePermiso("GESTIONAR_INVENTARIO")) {
            mostrarAdvertencia("Acceso Denegado", "No cuentas con permiso para gestionar el inventario.");
            return;
        }
        cargarVista("/org/paginalibre8/view/style/RegistrarSalidaInventarioView.fxml");
    }

    @FXML
    private void handleCategorias() {
        if (!SesionUsuario.getInstancia().tienePermiso("GESTIONAR_INVENTARIO")) {
            mostrarAdvertencia("Acceso Denegado", "No cuentas con permiso para gestionar categorías.");
            return;
        }
        cargarVista("/org/paginalibre8/view/style/BuscadorLibrosView.fxml");
    }

    @FXML
    private void handleCambiarPassword() {
        cargarVista("/org/paginalibre8/view/style/CambioPasswordView.fxml");
    }

    @FXML
    private void handleSalir() {
        SesionUsuario.getInstancia().cerrarSesion();
        try {
            Main.cambiarEscena("/org/paginalibre8/view/style/InicioSesionView.fxml");
        } catch (Exception e) {
            mostrarError("Error al cerrar sesión: " + e.getMessage());
        }
    }

    private void cargarVista(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent vista = loader.load();
            if (contentArea != null) {
                contentArea.getChildren().setAll(vista);
            }
        } catch (Exception e) {
            mostrarError("Error al cargar la vista (" + fxmlPath + "):\n" + e.getMessage());
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
