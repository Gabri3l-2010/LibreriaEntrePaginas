package org.paginalibre8.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.paginalibre8.dao.impl.UsuarioDAO;
import org.paginalibre8.model.Usuario;
import org.paginalibre8.system.Main;
import org.paginalibre8.util.SecurityUtil;


public class InicioSesionController implements Initializable {
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnIniciarSesion;
    @FXML private Button btnRegistrarse;
    @FXML private Label lblMensaje;
    @FXML private UsuarioDAO usuarioDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        usuarioDAO = new UsuarioDAO();
        lblMensaje.setText("");
    }

    @FXML
    public void eventoInicioSesion(ActionEvent evento) {
        String usuario = txtUsuario.getText();
        String password = txtPassword.getText();
        if (usuario.isEmpty() || password.isEmpty()) {
            lblMensaje.setText("Por favor, complete todos sus datos.");
            return;
        }
        String passwordHash = SecurityUtil.hashSHA256(password);
        Usuario usuarioIniciado = usuarioDAO.iniciarSesion(usuario, passwordHash);
        if (usuarioIniciado != null) {
            org.paginalibre8.servicio.SesionUsuario.getInstancia().iniciarSesion(usuarioIniciado);
            lblMensaje.setText("Inicio correcto");
            abrirDashBoard(usuarioIniciado);
        } else {
            lblMensaje.setText("Usuario o contraseña incorrectos");
        }
    }

    @FXML
    public void eventoIrRegistro(ActionEvent evento) {
        try {
            Main.cambiarEscena("/org/paginalibre8/view/style/RegistrarUsuarioView.fxml");
        } catch (Exception e) {
            System.err.println("Error al cargar el registro: " + e.getMessage());
            lblMensaje.setText("Error interno");
        }
    }

    private void abrirDashBoard(Usuario usuario) {
        String rutaFXML = "/org/paginalibre8/view/style/MenuPrincipalDashboardView.fxml";
        String tituloDashboard = "Panel Principal";
        
        if (usuario.getRol() != null) {
            switch (usuario.getRol().toLowerCase()) {
                case "admin":
                case "administrador":
                    rutaFXML = "/org/paginalibre8/view/style/MenuPrincipalDashboardView.fxml";
                    tituloDashboard = "Panel de Administración - Librería Entre Páginas";
                    break;
                case "cajero":
                    rutaFXML = "/org/paginalibre8/view/style/DashboardCajeroView.fxml";
                    tituloDashboard = "Panel de Cajero - Librería Entre Páginas";
                    break;
                case "bodega":
                    rutaFXML = "/org/paginalibre8/view/style/DashboardBodegaView.fxml";
                    tituloDashboard = "Panel de Bodega - Librería Entre Páginas";
                    break;
                default:
                    rutaFXML = "/org/paginalibre8/view/style/MenuPrincipalDashboardView.fxml";
                    tituloDashboard = "Panel Principal - Librería Entre Páginas (" + usuario.getRol() + ")";
                    break;
            }
        }
        try {
            FXMLLoader cargadorFXML = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent raiz = cargadorFXML.load();
            DashboardController controlado = cargadorFXML.getController();
            if (controlado != null) {
                controlado.iniciarUsuario(usuario);
            }
            Stage escenario = Main.getEscenarioPrincipal();
            escenario.setScene(new Scene(raiz));
            escenario.setTitle(tituloDashboard);
            escenario.show();
        } catch (IOException e) {
            System.err.println("Error al cargar la vista:" + rutaFXML + e.getMessage());
            lblMensaje.setText("Error interno al cargar la vista principal");
        }
    }    
}