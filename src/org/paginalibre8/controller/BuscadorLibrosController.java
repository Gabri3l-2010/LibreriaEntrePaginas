package org.paginalibre8.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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

public class BuscadorLibrosController implements Initializable {

    @FXML private TextField txtBuscarIsbn;
    @FXML private TextField txtBuscarTitulo;
    @FXML private TextField txtBuscarAutor;

    @FXML private TableView<Libro> tblLibros;
    @FXML private TableColumn<Libro, String> colIsbn;
    @FXML private TableColumn<Libro, String> colTitulo;
    @FXML private TableColumn<Libro, String> colAutor;
    @FXML private TableColumn<Libro, Double> colPrecio;
    @FXML private TableColumn<Libro, Integer> colStock;
    @FXML private TableColumn<Libro, String> colFecha;
    @FXML private TableColumn<Libro, String> colEditorial;

    private final LibroDAO libroDAO = new LibroDAOImpl();
    private final ObservableList<Libro> listaLibros = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarTodosLosLibros();
    }

    private void configurarTabla() {
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaPublicacion"));
        colEditorial.setCellValueFactory(new PropertyValueFactory<>("nitEditorial"));

        tblLibros.setItems(listaLibros);
    }

    private void cargarTodosLosLibros() {
        listaLibros.clear();
        List<Libro> libros = libroDAO.ListarTodos();
        if (libros != null && !libros.isEmpty()) {
            listaLibros.addAll(libros);
        }
    }

    @FXML
    private void handleBuscarPorIsbn(ActionEvent event) {
        String isbn = txtBuscarIsbn.getText() != null ? txtBuscarIsbn.getText().trim() : "";
        if (isbn.isEmpty()) {
            mostrarAdvertencia("Filtro vacío", "Por favor ingresa un ISBN para buscar.");
            return;
        }

        Libro libroEncontrado = libroDAO.buscarPorIsbn(isbn);
        listaLibros.clear();
        if (libroEncontrado != null) {
            listaLibros.add(libroEncontrado);
        } else {
            mostrarInfo("Sin Resultados", "No existe ningún libro registrado con el ISBN proporcionado.");
        }
    }

    @FXML
    private void handleBuscarPorTitulo(ActionEvent event) {
        String titulo = txtBuscarTitulo.getText() != null ? txtBuscarTitulo.getText().trim() : "";
        if (titulo.isEmpty()) {
            mostrarAdvertencia("Filtro vacío", "Por favor ingresa un título o palabra clave para buscar.");
            return;
        }

        List<Libro> librosEncontrados = libroDAO.buscarPorTitulo(titulo);
        listaLibros.clear();
        if (librosEncontrados != null && !librosEncontrados.isEmpty()) {
            listaLibros.addAll(librosEncontrados);
        } else {
            mostrarInfo("Sin Resultados", "No se encontraron libros que coincidan con la búsqueda por título.");
        }
    }

    @FXML
    private void handleBuscarPorAutor(ActionEvent event) {
        String autor = txtBuscarAutor.getText() != null ? txtBuscarAutor.getText().trim() : "";
        if (autor.isEmpty()) {
            mostrarAdvertencia("Filtro vacío", "Por favor ingresa el nombre de un autor para buscar.");
            return;
        }

        List<Libro> librosEncontrados = libroDAO.buscarPorAutor(autor);
        listaLibros.clear();
        if (librosEncontrados != null && !librosEncontrados.isEmpty()) {
            listaLibros.addAll(librosEncontrados);
        } else {
            mostrarInfo("Sin Resultados", "No se encontraron libros registrados para ese autor.");
        }
    }

    @FXML
    private void handleListarTodos(ActionEvent event) {
        handleLimpiar(event);
    }

    @FXML
    private void handleLimpiar(ActionEvent event) {
        txtBuscarIsbn.clear();
        txtBuscarTitulo.clear();
        txtBuscarAutor.clear();
        cargarTodosLosLibros();
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
}
