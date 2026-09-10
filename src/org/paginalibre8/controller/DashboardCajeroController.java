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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.paginalibre8.dao.impl.VentaDAO;
import org.paginalibre8.dao.impl.VentaDAOImpl;
import org.paginalibre8.model.Usuario;
import org.paginalibre8.model.Venta;
import org.paginalibre8.servicio.SesionUsuario;
import org.paginalibre8.system.Main;

public class DashboardCajeroController implements Initializable, DashboardController {

    @FXML private Label lblUsuario;
    @FXML private Label lblTotalVentasDia;
    @FXML private Label lblCantVentasDia;

    @FXML private TableView<Venta> tblVentasDia;
    @FXML private TableColumn<Venta, Integer> colIdVenta;
    @FXML private TableColumn<Venta, String> colFechaVenta;
    @FXML private TableColumn<Venta, String> colNitCliente;
    @FXML private TableColumn<Venta, Double> colTotalVenta;

    private final VentaDAO ventaDAO = new VentaDAOImpl();
    private final ObservableList<Venta> ventasDiaList = FXCollections.observableArrayList();
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

    /**
     * T2.29 - Tabla / Resumen de Ventas del Día
     */
    private void configurarTablaVentas() {
        if (tblVentasDia == null) return;
        colIdVenta.setCellValueFactory(new PropertyValueFactory<>("id"));
        colFechaVenta.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colNitCliente.setCellValueFactory(new PropertyValueFactory<>("nitCliente"));
        colTotalVenta.setCellValueFactory(new PropertyValueFactory<>("total"));

        tblVentasDia.setItems(ventasDiaList);
    }

    /**
     * T2.27 & T2.29 - Carga y cálculo de resumen de ventas del día
     */
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

    /**
     * T2.30 - Integración con el Punto de Venta
     */
    @FXML
    private void handleVentas(ActionEvent event) {
        if (!SesionUsuario.getInstancia().tienePermiso("VENDER")) {
            mostrarAdvertencia("Acceso Denegado", "No cuentas con permiso para realizar ventas.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/paginalibre8/view/style/VentaView.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Punto de Venta - Librería Entre Páginas");
            stage.setScene(new Scene(root, 940, 660));
            stage.showAndWait();
            cargarVentasDelDia();
        } catch (Exception e) {
            mostrarError("Error al abrir el Punto de Venta:\n" + e.getMessage());
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
