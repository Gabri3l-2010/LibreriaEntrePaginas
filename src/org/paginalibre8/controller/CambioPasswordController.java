package org.paginalibre8.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import org.paginalibre8.dao.impl.UsuarioDAO;
import org.paginalibre8.model.Usuario;
import org.paginalibre8.servicio.SesionUsuario;
import org.paginalibre8.util.SecurityUtil;

public class CambioPasswordController implements Initializable {

    @FXML private PasswordField txtPasswordActual;
    @FXML private PasswordField txtNuevaPassword;
    @FXML private PasswordField txtConfirmarPassword;
    @FXML private Label lblMensaje;

    private final UsuarioDAO usuarioDao = new UsuarioDAO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (lblMensaje != null) {
            lblMensaje.setText("");
        }
    }

    @FXML
    private void onGuardar(ActionEvent event) {
        String passwordActual = txtPasswordActual.getText();
        String nuevaPassword = txtNuevaPassword.getText();
        String confirmarPassword = txtConfirmarPassword.getText();

        // 1. Validar campos no vacíos (T1.26)
        if (passwordActual.isEmpty() || nuevaPassword.isEmpty() || confirmarPassword.isEmpty()) {
            mostrarMensaje("Todos los campos son obligatorios.");
            return;
        }

        // 2. Validar longitud mínima de la nueva contraseña (T1.26)
        if (nuevaPassword.length() < 6) {
            mostrarMensaje("La nueva contraseña debe tener al menos 6 caracteres.");
            txtNuevaPassword.requestFocus();
            return;
        }

        // 3. Validar coincidencia de nueva contraseña y confirmación (T1.26)
        if (!nuevaPassword.equals(confirmarPassword)) {
            mostrarMensaje("La nueva contraseña y la confirmación no coinciden.");
            txtConfirmarPassword.requestFocus();
            return;
        }

        // Obtener usuario en sesión
        Usuario usuarioActual = SesionUsuario.getInstancia().getUsuarioActual();
        if (usuarioActual == null) {
            mostrarMensaje("No hay una sesión de usuario activa.");
            return;
        }

        // 4. Validar contraseña actual ingresada (T1.24)
        String hashActualIngresado = SecurityUtil.hashSHA256(passwordActual);
        boolean passwordValida = usuarioDao.validarPasswordActual(usuarioActual.getId(), hashActualIngresado);

        if (!passwordValida) {
            mostrarMensaje("La contraseña actual es incorrecta.");
            txtPasswordActual.requestFocus();
            return;
        }

        // 5. Actualizar el hash de la nueva contraseña en la base de datos (T1.25)
        String hashNuevaPassword = SecurityUtil.hashSHA256(nuevaPassword);
        boolean cambiada = usuarioDao.cambiarPassword(usuarioActual.getId(), hashNuevaPassword);

        if (cambiada) {
            mostrarAlertaInfo("Cambio de Contraseña", "Tu contraseña ha sido actualizada exitosamente.");
            cerrarVentana();
        } else {
            mostrarMensaje("No fue posible cambiar la contraseña. Inténtalo de nuevo.");
        }
    }

    @FXML
    private void onCancelar(ActionEvent event) {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtPasswordActual.getScene().getWindow();
        stage.close();
    }

    private void mostrarMensaje(String texto) {
        if (lblMensaje != null) {
            lblMensaje.setText(texto);
        } else {
            mostrarAlertaAdvertencia("Validación", texto);
        }
    }

    private void mostrarAlertaInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAlertaAdvertencia(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
