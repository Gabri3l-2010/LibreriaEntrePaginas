package org.paginalibre8.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.paginalibre8.dao.impl.UsuarioDAO;
import org.paginalibre8.model.Usuario;
import org.paginalibre8.util.SecurityUtil;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UsuariosController implements Initializable {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmar;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtCorreo;
    @FXML private ComboBox<String> cmbRol;
    @FXML private ComboBox<String> cmbEstado;

    @FXML private TableView<Usuario> tblUsuarios;
    @FXML private TableColumn<Usuario, Number> colId;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colApellido;
    @FXML private TableColumn<Usuario, String> colUsuario;
    @FXML private TableColumn<Usuario, String> colCorreo;
    @FXML private TableColumn<Usuario, String> colRol1;
    @FXML private TableColumn<Usuario, String> colEstado;
    @FXML private TableColumn<Usuario, Void> colAccion;

    // 1. Instanciamos TU DAO original
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cmbRol.getItems().addAll("Administrador", "Cajero", "Bodega", "Empleado");
        cmbEstado.getItems().addAll("Activo", "Inactivo");
        cmbEstado.setValue("Activo");

        // 2. Configuramos las columnas para que lean directamente los Getters de tu clase Usuario
        colId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()));
        colUsuario.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
        colApellido.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getApellido()));
        colRol1.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRol()));
        colCorreo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCorreo()));
        
        colEstado.setCellValueFactory(cellData -> {
            boolean activo = cellData.getValue().isActivo();
            return new SimpleStringProperty(activo ? "Activo" : "Inactivo");
        });

        // 3. Cargamos los datos de MySQL a la tabla
        cargarDatosDesdeBD();

        // 4. Evento para pasar los datos de la fila seleccionada al formulario
        tblUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtUsername.setText(newSelection.getUsername()); 
                txtPassword.clear();
                txtConfirmar.clear();
                txtNombre.setText(valorSeguro(newSelection.getNombre()));
                txtApellido.setText(valorSeguro(newSelection.getApellido()));
                txtCorreo.setText(newSelection.getCorreo());
                cmbRol.setValue(newSelection.getRol());
                cmbEstado.setValue(newSelection.isActivo() ? "Activo" : "Inactivo");
            }
        });
    }

    private void cargarDatosDesdeBD() {
        listaUsuarios.clear();
        // Llamamos al método de TU DAO
        List<Usuario> usuariosBD = usuarioDAO.listarUsuarios(); 
        listaUsuarios.addAll(usuariosBD);
        tblUsuarios.setItems(listaUsuarios);
    }

    @FXML
    private void guardarUsuario() {
        if (!validarCampos(true)) return;

        String username = txtUsername.getText().trim();
        if (usuarioDAO.existeUsername(username)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Usuario duplicado", "El nombre de usuario ya existe.");
            txtUsername.requestFocus();
            return;
        }

        boolean guardado = usuarioDAO.registrarUsuario(username,
                SecurityUtil.hashSHA256(txtPassword.getText()), cmbRol.getValue(),
                txtNombre.getText().trim(), txtApellido.getText().trim(), txtCorreo.getText().trim());
        if (guardado) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Usuario guardado", "El usuario se registró correctamente.");
            cargarDatosDesdeBD();
            limpiarCampos();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo guardar", "Revisa la conexión y los datos ingresados.");
        }
    }

    @FXML
    private void modificarUsuario() {
        Usuario seleccionado = tblUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecciona un usuario", "Selecciona una fila para modificarla.");
            return;
        }
        if (!validarCampos(false)) return;

        String username = txtUsername.getText().trim();
        Usuario conMismoUsername = usuarioDAO.buscarPorUsername(username);
        if (conMismoUsername != null && conMismoUsername.getId() != seleccionado.getId()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Usuario duplicado", "El nombre de usuario ya está en uso.");
            return;
        }

        seleccionado.setUsername(username);
        seleccionado.setNombre(txtNombre.getText().trim());
        seleccionado.setApellido(txtApellido.getText().trim());
        seleccionado.setCorreo(txtCorreo.getText().trim());
        seleccionado.setRol(cmbRol.getValue());
        seleccionado.setActivo("Activo".equals(cmbEstado.getValue()));

        boolean actualizado = usuarioDAO.actualizarUsuario(seleccionado);
        if (actualizado && !txtPassword.getText().isEmpty()) {
            actualizado = usuarioDAO.cambiarPassword(seleccionado.getId(), SecurityUtil.hashSHA256(txtPassword.getText()));
        }
        if (actualizado) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Usuario actualizado", "Los cambios se guardaron correctamente.");
            cargarDatosDesdeBD();
            limpiarCampos();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo actualizar", "Revisa la conexión y vuelve a intentarlo.");
        }
    }

    @FXML
    private void eliminarUsuario() {
        Usuario seleccionado = tblUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selecciona un usuario", "Selecciona una fila para eliminarla.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                "El usuario '" + seleccionado.getUsername() + "' será desactivado. Podrás reactivarlo después.",
                ButtonType.YES, ButtonType.NO);
        confirmacion.setTitle("Desactivar usuario");
        confirmacion.setHeaderText(null);
        if (confirmacion.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) return;

        if (usuarioDAO.desactivarUsuario(seleccionado.getId())) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Usuario desactivado", "El usuario fue desactivado correctamente.");
            cargarDatosDesdeBD();
            limpiarCampos();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo eliminar", "No fue posible desactivar el usuario.");
        }
    }

    @FXML
    private void limpiarCampos() {
        txtUsername.clear();
        txtPassword.clear();
        txtConfirmar.clear();
        txtNombre.clear();
        txtApellido.clear();
        txtCorreo.clear();
        cmbRol.setValue(null);
        cmbEstado.setValue("Activo");
        tblUsuarios.getSelectionModel().clearSelection();
    }

    private boolean validarCampos(boolean esNuevo) {
        if (vacio(txtUsername) || vacio(txtNombre) || vacio(txtApellido) || vacio(txtCorreo)
                || cmbRol.getValue() == null || cmbEstado.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos incompletos", "Completa todos los campos obligatorios.");
            return false;
        }
        if (!txtUsername.getText().trim().matches("[A-Za-z0-9._-]{4,20}")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Usuario inválido", "Usa de 4 a 20 letras, números, puntos, guiones o guiones bajos.");
            return false;
        }
        if ((esNuevo || !txtPassword.getText().isEmpty()) && txtPassword.getText().length() < 6) {
            mostrarAlerta(Alert.AlertType.WARNING, "Contraseña inválida", "La contraseña debe tener al menos 6 caracteres.");
            return false;
        }
        if ((esNuevo || !txtPassword.getText().isEmpty() || !txtConfirmar.getText().isEmpty())
                && !txtPassword.getText().equals(txtConfirmar.getText())) {
            mostrarAlerta(Alert.AlertType.WARNING, "Contraseñas diferentes", "La contraseña y su confirmación no coinciden.");
            return false;
        }
        if (!txtCorreo.getText().trim().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Correo inválido", "Ingresa un correo electrónico válido.");
            return false;
        }
        return true;
    }

    private boolean vacio(TextField campo) {
        return campo.getText() == null || campo.getText().trim().isEmpty();
    }

    private String valorSeguro(String valor) { return valor == null ? "" : valor; }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
