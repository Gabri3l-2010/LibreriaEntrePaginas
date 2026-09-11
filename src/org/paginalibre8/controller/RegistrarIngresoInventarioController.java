package org.paginalibre8.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.paginalibre8.dao.impl.LibroDAO;
import org.paginalibre8.dao.impl.LibroDAOImpl;
import org.paginalibre8.dao.impl.MovimientoInventarioDAO;
import org.paginalibre8.dao.impl.MovimientoInventarioDAOImpl;
import org.paginalibre8.model.Libro;
import org.paginalibre8.model.MovimientoInventario;
import org.paginalibre8.servicio.SesionUsuario;

public class RegistrarIngresoInventarioController implements Initializable {

    @FXML private ComboBox<Libro> cmbLibro;
    @FXML private TextField txtCantidad;
    @FXML private TextArea txtObservacion;
    @FXML private Label lblStockActual;

    private final LibroDAO libroDAO = new LibroDAOImpl();
    private final MovimientoInventarioDAO movimientoDAO = new MovimientoInventarioDAOImpl();
    private final ObservableList<Libro> libros = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarLibros();
        cmbLibro.setItems(libros);
        cmbLibro.setOnAction(event -> mostrarStockSeleccionado());
    }

    private void cargarLibros() {
        List<Libro> resultado = libroDAO.ListarTodos();
        libros.setAll(resultado == null ? FXCollections.<Libro>emptyObservableList() : resultado);
    }

    private void mostrarStockSeleccionado() {
        Libro libro = cmbLibro.getValue();
        lblStockActual.setText(libro == null ? "Stock actual: —"
                : "Stock actual: " + libro.getStock());
    }

    @FXML
    private void handleRegistrarIngreso() {
        Libro libro = cmbLibro.getValue();
        if (libro == null) {
            mostrar(Alert.AlertType.WARNING, "Libro requerido", "Selecciona el libro que ingresará al inventario.");
            return;
        }

        final int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
        } catch (NumberFormatException | NullPointerException e) {
            mostrar(Alert.AlertType.WARNING, "Cantidad inválida", "Ingresa una cantidad entera mayor que cero.");
            return;
        }
        if (cantidad <= 0) {
            mostrar(Alert.AlertType.WARNING, "Cantidad inválida", "La cantidad debe ser mayor que cero.");
            return;
        }
        if (!SesionUsuario.getInstancia().haySesionActiva()) {
            mostrar(Alert.AlertType.ERROR, "Sesión requerida", "Inicia sesión para registrar el ingreso.");
            return;
        }

        MovimientoInventario movimiento = new MovimientoInventario(
                libro.getIsbn(), "INGRESO", cantidad,
                SesionUsuario.getInstancia().getUsuarioActual().getId(),
                txtObservacion.getText() == null ? "" : txtObservacion.getText().trim());
        if (movimientoDAO.registrarIngresoTransaccional(movimiento)) {
            libro.setStock(libro.getStock() + cantidad);
            cmbLibro.getSelectionModel().select(libro);
            mostrarStockSeleccionado();
            txtCantidad.clear();
            txtObservacion.clear();
            mostrar(Alert.AlertType.INFORMATION, "Ingreso registrado",
                    "Se agregaron " + cantidad + " unidades y se registró el movimiento.");
        } else {
            mostrar(Alert.AlertType.ERROR, "No se pudo registrar",
                    "No se modificó el stock. Verifica que el libro exista e intenta nuevamente.");
        }
    }

    @FXML
    private void handleLimpiar() {
        cmbLibro.getSelectionModel().clearSelection();
        txtCantidad.clear();
        txtObservacion.clear();
        mostrarStockSeleccionado();
    }

    private void mostrar(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
