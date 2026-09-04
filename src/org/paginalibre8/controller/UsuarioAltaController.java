package org.paginalibre8.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.paginalibre8.dao.impl.UsuarioDAO;
import org.paginalibre8.model.Usuario;
import org.paginalibre8.util.SecurityUtil;


public class UsuarioAltaController {
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmar;
    @FXML private ComboBox<String> cmbRol;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtCorreo;

    private final UsuarioDAO usuarioDao = new UsuarioDAO();
    private Usuario usuarioEditar = null;

    @FXML
    private void initialize() {
        cmbRol.getItems().addAll("Administrador", "Cajero", "Bodega", "Empleado");
        cmbRol.getSelectionModel().selectFirst();
    }

    public void setUsuarioParaEditar(Usuario usuario) {
        this.usuarioEditar = usuario;
        if (usuario != null) {
            txtUsername.setText(usuario.getUsername());
            txtUsername.setDisable(true); // El username no se suele cambiar
            txtNombre.setText(usuario.getNombre());
            txtApellido.setText(usuario.getApellido());
            txtCorreo.setText(usuario.getCorreo());
            cmbRol.setValue(usuario.getRol());
            // En modo edición, la contraseña no es obligatoria
            txtPassword.setPromptText("Opcional: Dejar en blanco para no cambiar");
            txtConfirmar.setPromptText("Opcional: Dejar en blanco para no cambiar");
        }
    }

    @FXML
    private void onGuardar(ActionEvent event) {
        if (!validarCampos()) return;

        String username = txtUsername.getText().trim();
        if (usuarioEditar == null && usuarioDao.existeUsername(username)) {
            mostrarAdvertencia("Usuario duplicado", "El nombre de usuario ya existe.");
            txtUsername.requestFocus();
            return;
        }

        boolean guardado;
        if (usuarioEditar != null) {
            usuarioEditar.setNombre(txtNombre.getText().trim());
            usuarioEditar.setApellido(txtApellido.getText().trim());
            usuarioEditar.setCorreo(txtCorreo.getText().trim());
            usuarioEditar.setRol(cmbRol.getValue());
            guardado = usuarioDao.actualizarUsuario(usuarioEditar);
        } else {
            guardado = usuarioDao.registrarUsuario(
                    username,
                    SecurityUtil.hashSHA256(txtPassword.getText()),
                    cmbRol.getValue(),
                    txtNombre.getText().trim(),
                    txtApellido.getText().trim(),
                    txtCorreo.getText().trim());
        }

        if (guardado) {
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Gestión de usuarios");
            alerta.setHeaderText(null);
            alerta.setContentText(usuarioEditar != null ? "Usuario actualizado correctamente." : "Usuario registrado correctamente.");
            alerta.showAndWait();
            cerrar();
        } else {
            mostrarError("No fue posible guardar el usuario. Revisa la conexión y la base de datos.");
        }
    }


    private boolean validarCampos() {
        if (vacio(txtUsername) || vacio(txtNombre) || vacio(txtApellido) || vacio(txtCorreo)
                || cmbRol.getValue() == null) {
            mostrarAdvertencia("Campos incompletos", "Por favor completa todos los campos obligatorios.");
            return false;
        }
        if (usuarioEditar == null && (vacio(txtPassword) || vacio(txtConfirmar))) {
            mostrarAdvertencia("Campos incompletos", "La contraseña es obligatoria para nuevos usuarios.");
            return false;
        }
        if (!txtUsername.getText().trim().matches("[A-Za-z0-9._-]{4,20}")) {
            mostrarAdvertencia("Usuario inválido", "Usa de 4 a 20 caracteres: letras, números, punto, guion o guion bajo.");
            txtUsername.requestFocus(); return false;
        }
        if (!txtPassword.getText().isEmpty() || usuarioEditar == null) {
            if (txtPassword.getText().length() < 6) {
                mostrarAdvertencia("Contraseña inválida", "La contraseña debe tener al menos 6 caracteres.");
                txtPassword.requestFocus(); return false;
            }
            if (!txtPassword.getText().equals(txtConfirmar.getText())) {
                mostrarAdvertencia("Contraseñas diferentes", "Las contraseñas no coinciden.");
                txtConfirmar.requestFocus(); return false;
            }
        }
        if (!txtNombre.getText().trim().matches("[A-Za-zÁÉÍÓÚáéíóúÑñ ]{2,40}")) {
            mostrarAdvertencia("Nombre inválido", "El nombre solo debe contener letras y espacios.");
            txtNombre.requestFocus(); return false;
        }
        if (!txtApellido.getText().trim().matches("[A-Za-zÁÉÍÓÚáéíóúÑñ ]{2,40}")) {
            mostrarAdvertencia("Apellido inválido", "El apellido solo debe contener letras y espacios.");
            txtApellido.requestFocus(); return false;
        }
        if (!txtCorreo.getText().trim().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            mostrarAdvertencia("Correo inválido", "Ingresa un correo electrónico válido.");
            txtCorreo.requestFocus(); return false;
        }
        return true;
    }


    private boolean vacio(TextField campo) { return campo.getText() == null || campo.getText().trim().isEmpty(); }
    private boolean vacio(PasswordField campo) { return campo.getText() == null || campo.getText().isEmpty(); }

    @FXML private void onCancelar(ActionEvent event) { cerrar(); }
    private void cerrar() { ((Stage) txtUsername.getScene().getWindow()).close(); }

    private void mostrarAdvertencia(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(titulo); a.setHeaderText(null); a.setContentText(mensaje); a.showAndWait();
    }
    private void mostrarError(String mensaje) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error"); a.setHeaderText(null); a.setContentText(mensaje); a.showAndWait();
    }
}