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
import org.paginalibre8.dao.impl.VentaDAO;
import org.paginalibre8.dao.impl.VentaDAOImpl;
import org.paginalibre8.model.DetalleVenta;
import org.paginalibre8.model.Libro;
import org.paginalibre8.model.Usuario;
import org.paginalibre8.model.Venta;
import org.paginalibre8.servicio.SesionUsuario;

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
    private final VentaDAO ventaDAO = new VentaDAOImpl();
    private final ObservableList<DetalleVenta> carritoList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTablaCarrito();
        configurarSpinner();
        if (txtNitCliente != null) {
            txtNitCliente.setText("C/F");
        }
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
     * T2.13 & T2.17 - Agregar productos y Validar stock disponible
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

        // T2.17: Validar stock disponible
        int cantidadEnCarrito = 0;
        DetalleVenta existente = null;
        for (DetalleVenta d : carritoList) {
            if (d.getIsbnLibro().equalsIgnoreCase(libro.getIsbn())) {
                existente = d;
                cantidadEnCarrito = d.getCantidad();
                break;
            }
        }

        if (libro.getStock() > 0 && (cantidadEnCarrito + cantidad) > libro.getStock()) {
            mostrarAdvertencia("Stock Insuficiente",
                    "El libro '" + libro.getTitulo() + "' solo cuenta con " + libro.getStock() +
                    " unidades disponibles en stock.");
            return;
        }

        if (existente != null) {
            existente.setCantidad(existente.getCantidad() + cantidad);
            tblCarrito.refresh();
        } else {
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
            d.calcularSubtotal();
            sumaTotal += d.getSubtotal();
        }
        sumaTotal = Math.round(sumaTotal * 100.0) / 100.0;
        if (lblTotal != null) {
            lblTotal.setText(String.format("Q %.2f", sumaTotal));
        }
    }

    /**
     * T2.18, T2.19, T2.20 & T2.21 - Procesar Venta con Transacción JDBC, Actualización de Stock, Rollback y Pruebas
     */
    @FXML
    private void handleProcesarVenta(ActionEvent event) {
        if (carritoList.isEmpty()) {
            mostrarAdvertencia("Carrito Vacío", "No hay productos en el carrito para procesar la venta.");
            return;
        }

        String nit = txtNitCliente.getText() != null ? txtNitCliente.getText().trim() : "C/F";
        if (nit.isEmpty()) {
            nit = "C/F";
        }

        int idUsuario = 1;
        if (SesionUsuario.getInstancia().haySesionActiva()) {
            Usuario u = SesionUsuario.getInstancia().getUsuarioActual();
            if (u != null && u.getId() > 0) {
                idUsuario = u.getId();
            }
        }

        Venta venta = new Venta(idUsuario, nit);
        for (DetalleVenta d : carritoList) {
            venta.agregarDetalle(d);
        }

        // T2.17: Validar stock final antes de la transacción
        if (!ventaDAO.validarStockVenta(venta)) {
            mostrarError("Error de Stock", "Uno o más productos del carrito no cuentan con suficiente stock disponible.");
            return;
        }

        // T2.18, T2.19 & T2.20: Transacción JDBC + Actualización de Stock + Rollback
        boolean registrada = ventaDAO.registrarVentaTransaccional(venta);

        if (registrada) {
            mostrarInfo("Venta Exitosa", "¡La venta #" + venta.getId() + " fue registrada exitosamente!\nTotal: Q" + String.format("%.2f", venta.getTotal()));
            carritoList.clear();
            txtNitCliente.setText("C/F");
            txtNombreCliente.clear();
            txtIsbn.clear();
            calcularTotal();
        } else {
            mostrarError("Error en Transacción", "No fue posible procesar la venta. Se ha revertido la operación (Rollback).");
        }
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
