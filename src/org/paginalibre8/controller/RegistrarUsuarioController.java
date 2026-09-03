package org.paginalibre8.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.paginalibre8.dao.impl.UsuarioDAO;
import org.paginalibre8.system.Main;
import org.paginalibre8.util.SecurityUtil;
import org.paginalibre8.util.ValidacionException;

public class RegistrarUsuarioController implements Initializable {

    @FXML private TextField txtUsuario; // Corregido: txtUsuario (sin 's' extra)
    @FXML private TextField txtPassword;
    @FXML private TextField txtConfirmarPassword;

    @FXML private Button btnRegistrar;
    @FXML private Button btnRegresar;
    @FXML private Label lblMensaje;
    private UsuarioDAO usuarioDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        usuarioDAO = new UsuarioDAO();
        if (lblMensaje != null) {
            lblMensaje.setText("");
        }
    }

    @FXML
    public void eventoRegistrar(ActionEvent evento) throws Exception {
        try {
            ValidacionException.validarNoVacio(txtUsuario.getText(), "usuario");
            ValidacionException.validarNoVacio(txtPassword.getText(), "contraseña");
            ValidacionException.validarNoVacio(txtConfirmarPassword.getText(), "confirmar contraseña");
            ValidacionException.validarCoincidencia(txtPassword.getText(), 
                    txtConfirmarPassword.getText(), "Las contraseñas no coinciden");
            ValidacionException.validarLongitudMinima(txtPassword.getText(), 6, 
                    "La contraseña debe tener al menos 6 caracteres");

            String usuario = txtUsuario.getText().trim();
            String password = txtPassword.getText();
            String passwordHash = SecurityUtil.hashSHA256(password);

            boolean registrado = usuarioDAO.registrarUsuario(usuario, passwordHash);
            if (registrado) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Usuario registrado con éxito");
                Main.cambiarEscena("/org/paginalibre8/view/style/InicioSesionView.fxml");               
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error al registrar. El usuario podría ya existir.");
            }
        } catch (ValidacionException e) {
            mostrarAlerta(Alert.AlertType.WARNING, e.getMessage());
            if (lblMensaje != null) lblMensaje.setText(e.getMessage());
        } catch (IOException e) {
            System.err.println("Error al volver al Login: " + e.getMessage());
        }
    }

    @FXML
    public void eventoRegresar(ActionEvent evento) throws Exception {
        try {
            Main.cambiarEscena("/org/paginalibre8/view/style/InicioSesionView.fxml");
        } catch (IOException e) {
            System.err.println("Error al cargar inicio de sesión: " + e.getMessage());
            if (lblMensaje != null) lblMensaje.setText("Error interno");
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alerta = new Alert(tipo, mensaje, ButtonType.OK);
        alerta.show(); 
    }
}