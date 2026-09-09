package org.paginalibre8.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.paginalibre8.dao.impl.LibroDAO;
import org.paginalibre8.dao.impl.LibroDAOImpl;
import org.paginalibre8.model.DetalleVenta;
import org.paginalibre8.model.Libro;

public class VentaController implements Initializable {

    @FXML private TextField txtNitCliente;
    @FXML private TextField txtNombreCliente;
    @FXML private TextField txtIsbn;
    @FXML private Spinner<Integer> spnCantidad;

    @FXML private TableView<DetalleVenta> tblCarrito;
    @FXML private TableColumn<DetalleVenta, String> colIsbn;
    @FXML private TableColumn<DetalleVenta, String> colTitulo;
    @FXML private TableColumn<DetalleVenta, Double> colPrecio;
    @FXML private TableColumn<DetalleVenta, Integer> colCantidad;
    @FXML private TableColumn<DetalleVenta, Double> colSubtotal;

    @FXML private Label lblTotal;

    private final LibroDAO libroDAO = new LibroDAOImpl();
    private final ObservableList<DetalleVenta> carritoList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTablaCarrito();
        configurarSpinner();
    }

    /**
     * T2.12 - Carrito de Venta
     */
    private void configurarTablaCarrito() {
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbnLibro"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("tituloLibro"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        tblCarrito.setItems(carritoList);
    }

    private void configurarSpinner() {
        if (spnCantidad != null) {
            SpinnerValueFactory<Integer> valueFactory =
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
            spnCantidad.setValueFactory(valueFactory);
        }
    }

    /**
     * T2.13 - Agregar/eliminar productos
     */
    @FXML
    private void handleAgregarProducto(ActionEvent event) {
        String isbn = txtIsbn.getText() != null ? txtIsbn.getText().trim() : "";
        if (isbn.isEmpty()) {
            mostrarAdvertencia("Campo Requerido", "Por favor ingresa un ISBN para buscar el producto.");
            return;
        }

        Libro libro = libroDAO.buscarPorIsbn(isbn);
        if (libro == null) {
            mostrarError("Libro No Encontrado", "No existe un libro con el ISBN proporcionado.");
            return;
        }

        int cantidad = spnCantidad.getValue() != null ? spnCantidad.getValue() : 1;

        // Verificar si ya está en el carrito (T2.14)
        DetalleVenta existente = null;
        for (DetalleVenta d : carritoList) {
            if (d.getIsbnLibro().equalsIgnoreCase(libro.getIsbn())) {
                existente = d;
                break;
            }
        }

        if (existente != null) {
            // Actualizar cantidad (T2.14) y recargar subtotal (T2.15)
            existente.setCantidad(existente.getCantidad() + cantidad);
            tblCarrito.refresh();
        } else {
            // Agregar nuevo ítem al carrito (T2.13)
            DetalleVenta nuevo = new DetalleVenta(libro.getIsbn(), libro.getTitulo(), cantidad, libro.getPrecio());
            carritoList.add(nuevo);
        }

        txtIsbn.clear();
        spnCantidad.getValueFactory().setValue(1);
        calcularTotal();
    }

    /**
     * T2.13 - Eliminar productos del carrito
     */
    @FXML
    private void handleEliminarProducto(ActionEvent event) {
        DetalleVenta seleccionado = tblCarrito.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAdvertencia("Selección Requerida", "Por favor selecciona un producto del carrito para quitarlo.");
            return;
        }
        carritoList.remove(seleccionado);
        calcularTotal();
    }

    /**
     * T2.13 - Vaciar todo el carrito
     */
    @FXML
    private void handleVaciarCarrito(ActionEvent event) {
        carritoList.clear();
        calcularTotal();
    }

    /**
     * T2.15 & T2.16 - Calcular Subtotal y Total de Venta
     */
    private void calcularTotal() {
        double sumaTotal = 0.0;
        for (DetalleVenta d : carritoList) {
            d.calcularSubtotal(); // T2.15: Calcular subtotal
            sumaTotal += d.getSubtotal();
        }
        sumaTotal = Math.round(sumaTotal * 100.0) / 100.0; // T2.16: Calcular total
        if (lblTotal != null) {
            lblTotal.setText(String.format("Q %.2f", sumaTotal));
        }
    }

    @FXML
    private void handleProcesarVenta(ActionEvent event) {
        if (carritoList.isEmpty()) {
            mostrarAdvertencia("Carrito Vacío", "No hay productos en el carrito para procesar la venta.");
            return;
        }
        mostrarInfo("Procesando Venta", "El carrito cuenta con " + carritoList.size() + " producto(s). Listo para validación de transacción.");
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

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
