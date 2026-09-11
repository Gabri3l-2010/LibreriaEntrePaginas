package org.paginalibre8.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.paginalibre8.dao.impl.LibroDAO;
import org.paginalibre8.dao.impl.LibroDAOImpl;
import org.paginalibre8.model.Libro;

public class BodegaResumenController implements Initializable {

    @FXML private Label lblContadorCritico;
    @FXML private TableView<Libro> tblStockCritico;
    @FXML private TableColumn<Libro, String> colIsbn;
    @FXML private TableColumn<Libro, String> colTitulo;
    @FXML private TableColumn<Libro, Integer> colStockActual;
    @FXML private TableColumn<Libro, Integer> colStockMinimo;
    @FXML private TableColumn<Libro, Void> colAccion;

    private final LibroDAO libroDAO = new LibroDAOImpl();
    private final ObservableList<Libro> listaLibros = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarDatosCriticos();
    }

    private void configurarTabla() {
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colStockActual.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colStockMinimo.setCellValueFactory(new PropertyValueFactory<>("stockMinimo"));

        agregarBotonAccion();
        tblStockCritico.setItems(listaLibros);
    }

    private void cargarDatosCriticos() {
        listaLibros.clear();
        List<Libro> libros = libroDAO.obtenerLibrosConStockCritico();
        if (libros != null) {
            listaLibros.addAll(libros);
        }
        
        if (lblContadorCritico != null) {
            lblContadorCritico.setText(String.valueOf(listaLibros.size()));
        }
    }

    private void agregarBotonAccion() {
        Callback<TableColumn<Libro, Void>, TableCell<Libro, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Libro, Void> call(final TableColumn<Libro, Void> param) {
                return new TableCell<>() {
                    private final Button btnRevisar = new Button("Revisar");

                    {
                        btnRevisar.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-cursor: hand; -fx-padding: 4 10; -fx-background-radius: 4;");
                        btnRevisar.setOnAction(event -> {
                            Libro libro = getTableView().getItems().get(getIndex());
                            abrirFichaLibro(libro);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btnRevisar);
                        }
                    }
                };
            }
        };
        colAccion.setCellFactory(cellFactory);
    }

    private void abrirFichaLibro(Libro libro) {
        // Por ahora simularemos la apertura de la ficha
        // porque el DashboardController principal maneja las vistas.
        // Mostramos un alert de la ficha del libro, o podríamos cargar la vista de inventario.
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Ficha del Libro");
        alert.setHeaderText("ISBN: " + libro.getIsbn() + " | Título: " + libro.getTitulo());
        alert.setContentText("Stock actual: " + libro.getStock() + "\nStock mínimo: " + libro.getStockMinimo() + "\n\nSe recomienda realizar un ingreso de inventario pronto.");
        alert.showAndWait();
    }
}
