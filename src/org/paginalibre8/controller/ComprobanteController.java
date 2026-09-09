package org.paginalibre8.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.paginalibre8.model.DetalleVenta;
import org.paginalibre8.model.Venta;
import org.paginalibre8.servicio.SesionUsuario;

public class ComprobanteController implements Initializable {

    @FXML private VBox vboxTicket;
    @FXML private Label lblNoComprobante;
    @FXML private Label lblFecha;
    @FXML private Label lblCajero;
    @FXML private Label lblNitCliente;
    @FXML private Label lblTotalVenta;

    @FXML private TableView<DetalleVenta> tblDetallesComprobante;
    @FXML private TableColumn<DetalleVenta, Integer> colCant;
    @FXML private TableColumn<DetalleVenta, String> colTitulo;
    @FXML private TableColumn<DetalleVenta, Double> colPrecio;
    @FXML private TableColumn<DetalleVenta, Double> colSubtotal;

    private final ObservableList<DetalleVenta> detallesList = FXCollections.observableArrayList();
    private Venta ventaActual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTablaComprobante();
    }

    private void configurarTablaComprobante() {
        colCant.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("tituloLibro"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        tblDetallesComprobante.setItems(detallesList);
    }

    /**
     * Carga y genera el comprobante a partir del objeto Venta
     */
    public void cargarComprobante(Venta venta) {
        this.ventaActual = venta;
        if (venta == null) return;

        lblNoComprobante.setText("#" + String.format("%06d", venta.getId()));
        lblFecha.setText(venta.getFecha() != null && !venta.getFecha().isEmpty() ? venta.getFecha() : "2026-09-09");
        
        String cajero = SesionUsuario.getInstancia().haySesionActiva()
                ? SesionUsuario.getInstancia().getNombreCompleto()
                : (venta.getUsernameUsuario() != null ? venta.getUsernameUsuario() : "Cajero General");
        lblCajero.setText(cajero);

        lblNitCliente.setText(venta.getNitCliente() != null && !venta.getNitCliente().isEmpty() ? venta.getNitCliente() : "C/F");
        lblTotalVenta.setText(String.format("Q %.2f", venta.getTotal()));

        detallesList.clear();
        if (venta.getDetalles() != null) {
            detallesList.addAll(venta.getDetalles());
        }
    }

    @FXML
    private void handleImprimir(ActionEvent event) {
        if (vboxTicket == null) return;

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            boolean proceed = job.showPrintDialog(vboxTicket.getScene().getWindow());
            if (proceed) {
                boolean printed = job.printPage(vboxTicket);
                if (printed) {
                    job.endJob();
                    mostrarInfo("Impresión Exitosa", "El comprobante ha sido enviado a la impresora.");
                } else {
                    mostrarError("Error de Impresión", "No se pudo completar la impresión del ticket.");
                }
            }
        } else {
            mostrarInfo("Impresión Simulada", "Generación e impresión de ticket completada con éxito.");
        }
    }

    @FXML
    private void handleCerrar(ActionEvent event) {
        Stage stage = (Stage) vboxTicket.getScene().getWindow();
        stage.close();
    }

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
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
