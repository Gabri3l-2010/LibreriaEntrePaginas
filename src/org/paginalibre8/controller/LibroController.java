/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.paginalibre8.controller;


import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.paginalibre8.dao.impl.LibroDAO;
import org.paginalibre8.dao.impl.LibroDAOImpl;
import org.paginalibre8.model.Libro;
import org.paginalibre8.system.Main;


public class LibroController implements Initializable {

    @FXML
    private TextField txtIsbn;
    @FXML
    private TextField txtTitulo;
    @FXML
    private TextField txtFechaPublicacion;
    @FXML
    private TextField txtPrecio;
    @FXML
    private TextField txtIdCategoria;
    @FXML
    private TextField txtNitEditorial;
    @FXML
    private TableView<Libro> tablaLibros;
    @FXML
    private TableColumn<Libro, String> colIsbn;
    @FXML
    private TableColumn<Libro, String> colTitulo;
    @FXML
    private TableColumn<Libro, String> colFechaPublicacion;
    @FXML
    private TableColumn<Libro, Double> colPrecio;
    @FXML
    private TableColumn<Libro, Integer> colIdCategoria;
    @FXML
    private TableColumn<Libro, String> colNitEditorial;
    @FXML
    private Label lblMensaje;

    private final LibroDAO libroDAO = new LibroDAOImpl();
    private final ObservableList<Libro> listaLibros
            = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarColumnas();
        cargarTabla();
        seleccionarFila();
    }

    private void configurarColumnas() {
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colFechaPublicacion.setCellValueFactory(new PropertyValueFactory<>("fechaPublicacion"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colIdCategoria.setCellValueFactory(new PropertyValueFactory<>("idCategoria"));
        colNitEditorial.setCellValueFactory(new PropertyValueFactory<>("nitEditorial"));
    }

    private void cargarTabla() {
        listaLibros.setAll(libroDAO.ListarTodos());
        tablaLibros.setItems(listaLibros);
    }

    private void seleccionarFila() {
        tablaLibros.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        txtIsbn.setText(newSelection.getIsbn());
                        txtTitulo.setText(newSelection.getTitulo());
                        txtFechaPublicacion.setText(newSelection.getFechaPublicacion());
                        txtPrecio.setText(String.valueOf(newSelection.getPrecio()));
                        txtIdCategoria.setText(String.valueOf(newSelection.getIdCategoria()));
                        txtNitEditorial.setText(newSelection.getNitEditorial());
                    }
                });
    }

    @FXML
    private void handleGuardar() {
        try {
            if (txtIsbn.getText().isEmpty() || txtTitulo.getText().isEmpty()
                    || txtFechaPublicacion.getText().isEmpty()
                    || txtPrecio.getText().isEmpty()
                    || txtIdCategoria.getText().isEmpty()
                    || txtNitEditorial.getText().isEmpty()) {
                mostrarError("Todos los campos son obligatorios.");
                return;
            }
            Libro libro = new Libro();
            libro.setIsbn(txtIsbn.getText().trim());
            libro.setTitulo(txtTitulo.getText().trim());
            libro.setFechaPublicacion(txtFechaPublicacion.getText().trim());
            libro.setPrecio(Double.parseDouble(txtPrecio.getText().trim()));
            libro.setIdCategoria(Integer.parseInt(txtIdCategoria.getText().trim()));
            libro.setNitEditorial(txtNitEditorial.getText().trim());
            if (libroDAO.crear(libro)) {
                lblMensaje.setText("Libro registrado exitosamente.");
                cargarTabla();
                limpiarFormulario();
            } else {
                mostrarError("No se pudo registrar el libro.");
            }
        } catch (NumberFormatException e) {
            mostrarError("Precio e ID de categoría deben ser números válidos.");
        } catch (IllegalArgumentException e) {
            mostrarError("La fecha de publicación debe tener el formato AAAA-MM-DD.");
        } catch (Exception e) {
            mostrarError("Error al guardar: " + e.getMessage());
        }
    }

    @FXML
    private void handleLimpiar() {
        limpiarFormulario();
        lblMensaje.setText("");
    }

    @FXML
    private void handleActualizar() {
        cargarTabla();
        lblMensaje.setText("Tabla actualizada.");
    }

    @FXML
    private void handleVolver() {
        try {
            Main.cambiarEscena("/org/paginalibre8/view/style/MenuPrincipalDashboardView.fxml");
        } catch (Exception e) {
            mostrarError("Error al volver al menú: " + e.getMessage());
        }
    }

    private void limpiarFormulario() {
        txtIsbn.clear();
        txtTitulo.clear();
        txtFechaPublicacion.clear();
        txtPrecio.clear();
        txtIdCategoria.clear();
        txtNitEditorial.clear();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}