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

public class MenuPrincipalDashboardController implements Initializable, DashboardController {

    @FXML private Label lblUsuario;
    @FXML private StackPane contentArea;
    
    private Usuario usuarioActual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (SesionUsuario.getInstancia().haySesionActiva()) {
            this.usuarioActual = SesionUsuario.getInstancia().getUsuarioActual();
            if (lblUsuario != null) {
                lblUsuario.setText("Admin: " + SesionUsuario.getInstancia().getNombreCompleto());
            }
        }
        Platform.runLater(this::handleUsuarios);
    }

    @Override
    public void iniciarUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
        if (lblUsuario != null && usuario != null) {
            lblUsuario.setText("Admin: " + usuario.getUsername());
        }
        Platform.runLater(this::handleUsuarios);
    }

    @FXML
    private void handleUsuarios() {
        if (!SesionUsuario.getInstancia().esAdmin()) {
            mostrarAdvertencia("Acceso Denegado", "No cuentas con permisos suficientes para acceder a la Gestión de Usuarios.");
            return;
        }
        cargarVista("/org/paginalibre8/view/style/Usuarios.fxml");
    }

    @FXML
    private void handleLibros() {
        cargarVista("/org/paginalibre8/view/style/BuscadorLibrosView.fxml");
    }

    @FXML
    private void handleClientes() {
        mostrarInfo("Módulo de Clientes", "Gestión de clientes en desarrollo.");
    }

    @FXML
    private void handleAutores() {
        mostrarInfo("Módulo de Autores", "Gestión de autores en desarrollo.");
    }

    @FXML
    private void handleCategorias() {
        mostrarInfo("Módulo de Categorías", "Gestión de categorías en desarrollo.");
    }

    @FXML
    private void handleEditoriales() {
        mostrarInfo("Módulo de Editoriales", "Gestión de editoriales en desarrollo.");
    }

    @FXML
    private void handleCambiarPassword(ActionEvent event) {
        cargarVista("/org/paginalibre8/view/style/CambioPasswordView.fxml");
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
