package org.paginalibre8.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.paginalibre8.dao.impl.LibroDAO;
import org.paginalibre8.dao.impl.LibroDAOImpl;
import org.paginalibre8.dao.impl.VentaDAO;
import org.paginalibre8.dao.impl.VentaDAOImpl;
import org.paginalibre8.model.DetalleVenta;
import org.paginalibre8.model.Libro;
import org.paginalibre8.model.Usuario;
import org.paginalibre8.model.Venta;
import org.paginalibre8.servicio.SesionUsuario;
import org.paginalibre8.system.Main;

public class DashboardCajeroController implements Initializable, DashboardController {

    // ---- KPIs / cabecera ----
    @FXML private Label lblUsuario;
    @FXML private Label lblTotalVentasDia;
    @FXML private Label lblCantVentasDia;

    // ---- Tabla de ventas del día ----
    @FXML private TableView<Venta> tblVentasDia;
    @FXML private TableColumn<Venta, Integer> colIdVenta;
    @FXML private TableColumn<Venta, String> colFechaVenta;
    @FXML private TableColumn<Venta, String> colNitCliente;
    @FXML private TableColumn<Venta, Double> colTotalVenta;

    // ---- Formulario de Nueva Venta (embebido) ----
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

    private final VentaDAO ventaDAO = new VentaDAOImpl();
    private final LibroDAO libroDAO = new LibroDAOImpl();
    private final ObservableList<Venta> ventasDiaList = FXCollections.observableArrayList();
    private final ObservableList<DetalleVenta> carritoList = FXCollections.observableArrayList();
    private Usuario usuarioActual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (SesionUsuario.getInstancia().haySesionActiva()) {
            if (!SesionUsuario.getInstancia().tienePermiso("VENDER")) {
                mostrarAdvertencia("Acceso Denegado", "No cuentas con permisos para acceder al Panel de Cajero.");
                return;
            }
            this.usuarioActual = SesionUsuario.getInstancia().getUsuarioActual();
            if (lblUsuario != null) {
                lblUsuario.setText("Cajero: " + SesionUsuario.getInstancia().getNombreCompleto());
            }
        }
        configurarTablaVentas();
        configurarTablaCarrito();
        configurarSpinner();
        if (txtNitCliente != null) {
            txtNitCliente.setText("C/F");
        }
        cargarVentasDelDia();
    }

    @Override
    public void iniciarUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
        if (lblUsuario != null && usuario != null) {
            lblUsuario.setText("Cajero: " + usuario.getUsername());
        }
        cargarVentasDelDia();
    }


    private void configurarTablaVentas() {
        if (tblVentasDia == null) return;
        colIdVenta.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFechaVenta.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colNitCliente.setCellValueFactory(new PropertyValueFactory<>("nitCliente"));
        colTotalVenta.setCellValueFactory(new PropertyValueFactory<>("total"));

        tblVentasDia.setItems(ventasDiaList);
    }

    public void cargarVentasDelDia() {
        ventasDiaList.clear();
        List<Venta> ventas;
        if (usuarioActual != null && usuarioActual.getId() > 0) {
            ventas = ventaDAO.obtenerVentasDelDiaPorUsuario(usuarioActual.getId());
        } else {
            ventas = ventaDAO.obtenerVentasDelDia();
        }

        if (ventas != null) {
            ventasDiaList.addAll(ventas);
        }

        double totalSuma = 0.0;
        for (Venta v : ventasDiaList) {
            totalSuma += v.getTotal();
        }

        if (lblTotalVentasDia != null) {
            lblTotalVentasDia.setText(String.format("Q %.2f", totalSuma));
        }

        if (lblCantVentasDia != null) {
            lblCantVentasDia.setText(String.valueOf(ventasDiaList.size()));
        }
    }

    @FXML
    private void handleRefrescarVentas(ActionEvent event) {
        cargarVentasDelDia();
    }



    private void configurarTablaCarrito() {
        if (tblCarrito == null) return;
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

    @FXML
    private void handleAgregarProducto(ActionEvent event) {
        String isbn = txtIsbn.getText() != null ? txtIsbn.getText().trim() : "";
        if (isbn.isEmpty()) {
            mostrarAdvertencia("Campo Requerido", "Por favor ingresa un ISBN para buscar el producto.");
            return;
        }

        Libro libro = libroDAO.buscarPorIsbn(isbn);
        if (libro == null) {
            mostrarError("No existe un libro con el ISBN proporcionado.");
            return;
        }

        int cantidad = spnCantidad.getValue() != null ? spnCantidad.getValue() : 1;

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

    @FXML
    private void handleVaciarCarrito(ActionEvent event) {
        carritoList.clear();
        calcularTotal();
    }

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

        if (!ventaDAO.validarStockVenta(venta)) {
            mostrarError("Uno o más productos del carrito no cuentan con suficiente stock disponible.");
            return;
        }

        boolean registrada = ventaDAO.registrarVentaTransaccional(venta);

        if (registrada) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/paginalibre8/view/style/ComprobanteView.fxml"));
                Parent root = loader.load();
                ComprobanteController comprobanteCtrl = loader.getController();
                if (comprobanteCtrl != null) {
                    comprobanteCtrl.cargarComprobante(venta);
                }
                Stage stage = new Stage();
                stage.setTitle("Comprobante de Venta #" + venta.getId());
                stage.setScene(new Scene(root, 480, 620));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();
            } catch (Exception e) {
                System.err.println("Error al desplegar comprobante: " + e.getMessage());
            }

            mostrarInfo("Venta Exitosa", "¡La venta #" + venta.getId() + " fue registrada exitosamente!\nTotal: Q" + String.format("%.2f", venta.getTotal()));
            carritoList.clear();
            txtNitCliente.setText("C/F");
            txtNombreCliente.clear();
            txtIsbn.clear();
            calcularTotal();
            cargarVentasDelDia();
        } else {
            mostrarError("No fue posible procesar la venta. Se ha revertido la operación (Rollback).");
        }
    }


    @FXML
    private void handleClientes(ActionEvent event) {
        if (!SesionUsuario.getInstancia().tienePermiso("VENDER")) {
            mostrarAdvertencia("Acceso Denegado", "No cuentas con permiso para consultar clientes.");
            return;
        }
        mostrarInfo("Módulo de Clientes", "Abriendo consulta de clientes...");
    }

    @FXML
    private void handleCambiarPassword(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/paginalibre8/view/style/CambioPasswordView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Cambio de Contraseña");
            stage.setScene(new Scene(root, 420, 340));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            mostrarError("Error al abrir cambio de contraseña: " + e.getMessage());
        }
    }

    @FXML
    private void handleSalir(ActionEvent event) {
        SesionUsuario.getInstancia().cerrarSesion();
        try {
            Main.cambiarEscena("/org/paginalibre8/view/style/InicioSesionView.fxml");
        } catch (Exception e) {
            mostrarError("Error al cerrar sesión: " + e.getMessage());
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

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}