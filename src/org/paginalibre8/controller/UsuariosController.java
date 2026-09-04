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

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UsuariosController implements Initializable {

    @FXML private TextField txtId;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtUsername;
    @FXML private TextField txtCorreo;
    @FXML private TextField txtRol;
    @FXML private TextField txtRol1;
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
        cmbEstado.getItems().addAll("Activo", "Inactivo");

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
                txtId.setText(String.valueOf(newSelection.getId()));
                txtNombre.setText(newSelection.getNombre());
                txtApellido.setText(newSelection.getApellido());
                txtUsername.setText(newSelection.getUsername()); 
                txtCorreo.setText(newSelection.getCorreo());
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
        // Aquí irá tu lógica de guardar
    }

    @FXML
    private void modificarUsuario() {
        // Aquí irá tu lógica de actualizar
    }

    @FXML
    private void eliminarUsuario() {
        // Aquí irá tu lógica de desactivar/eliminar
    }

    @FXML
    private void limpiarCampos() {
        txtId.clear();
        txtNombre.clear();
        txtApellido.clear();
        txtUsername.clear();
        txtCorreo.clear();
        txtRol.clear();
        txtRol1.clear();
        cmbEstado.setValue(null);
        tblUsuarios.getSelectionModel().clearSelection();
    }
}