
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.paginalibre8.controller;


import java.io.IOException;
import javafx.fxml.FXML;
 
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
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
 
    @FXML private TextField txtUsusario;
    @FXML private TextField txtPassword;
    @FXML private TextField txtConfirmarPassword;
 
    @FXML private Button btnRegistrar;
    @FXML private Button btnRegresar;
    @FXML private Label lblMensaje;
    private UsuarioDAO usuarioDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        usuarioDAO = new UsuarioDAO();
        lblMensaje.setText("");
    }
 
    //eventoRegistrar
    @FXML


    public void eventoRegistrar(ActionEvent evento) throws Exception{
        try {
            ValidacionException.validarNoVacio(txtUsusario.getText(), "usuario");
            ValidacionException.validarNoVacio(txtPassword.getText(), "contrasseña");
            ValidacionException.validarNoVacio(txtConfirmarPassword.getText(), 
                    "confirmar contrasseña");
            ValidacionException.validarCoincidencia(txtPassword.getText(), 
                    txtConfirmarPassword.getText(), "Las contraseñas no coinciden");
            ValidacionException.validarLongitudMinima(txtPassword.getText(), 6, 
                    "La contraseña debe tener al menos 6 caracteres");
            String usuario = txtUsusario.getText().trim();
            String password = txtPassword.getText();
            String passwordHass = SecurityUtil.hashSHA256(password);
            boolean registrado = usuarioDAO.registrarUsuario(usuario, passwordHass);
            if (registrado) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Usuario registrado con exito");

                Main.cambiarEscena("/org/paginalibre8/view/style/InicioSesionView.fxml");                

            }else{
                mostrarAlerta(Alert.AlertType.ERROR, "Error al Registrar. El usuario podría ya existir.");
            }
        } catch (ValidacionException e) {
            mostrarAlerta(Alert.AlertType.WARNING, e.getMessage());
            lblMensaje.setText(e.getMessage());
        } catch (IOException e){
            System.err.println("Error al volver al Login: " + e.getMessage());
        }
    }
    //eventoRegresar
    @FXML

    public void eventoRegresar(ActionEvent evento) throws Exception{
         try {
            Main.cambiarEscena("/org/paginalibre8/view/style/InicioSesionView.fxml");

        } catch (IOException e) {
            System.err.println("Error al carger registro:  " +  e.getMessage());
            lblMensaje.setText("Error interno");
        }
    }
    //mostrarAlerta
    private void mostrarAlerta(Alert.AlertType tipo, String mensaje){
        Alert alerta = new Alert(tipo, mensaje, ButtonType.OK);
        alerta.show(); 
    }
}