/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.paginalibre8.system;
 
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
public class Main extends Application {
    private static Stage escenarioPrincipal;
    @Override
    public void start(Stage escenarioPrincipal) throws Exception {
        Main.escenarioPrincipal = escenarioPrincipal;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/paginalibre8/view/style/InicioSesionView.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        escenarioPrincipal.setTitle("Librería entre Paginas");
        escenarioPrincipal.setScene(scene);
        escenarioPrincipal.show();
    }
     public static void cambiarEscena(String fxmlPath) throws Exception {
        java.net.URL url = Main.class.getResource(fxmlPath);
        if (url == null) {
            throw new Exception("No se encontró el archivo FXML en la ruta: " + fxmlPath);
        }
        FXMLLoader loader = new FXMLLoader(url);
        Parent root = loader.load();
        if (escenarioPrincipal == null) {
            throw new Exception("El escenarioPrincipal no ha sido inicializado.");
        }
        escenarioPrincipal.setScene(new Scene(root));
    }
    public static Stage getEscenarioPrincipal() {
        return escenarioPrincipal;
    }
    public static void main(String[] args) {
        launch(args);
    }
}