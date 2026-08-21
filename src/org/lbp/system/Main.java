/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.lbp.system;

import javafx.application.Application;

import javafx.scene.Scene;

import javafx.scene.control.Label;

import javafx.scene.layout.StackPane;

import javafx.stage.Stage;

public class Main extends Application {

    @Override

    public void start(Stage primaryStage) {

        Label label = new Label("¡Hola, JavaFX!");

        StackPane root = new StackPane(label);

        Scene scene = new Scene(root, 300, 200);

        primaryStage.setTitle("Mi Primera App JavaFX");

        primaryStage.setScene(scene);

        primaryStage.show();

    }

    // El método main clásico
    public static void main(String[] args) {

        // Esta línea arranca la aplicación JavaFX
        launch(args);

    }

}
