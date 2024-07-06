package com.example.javafx_selenium;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            URL url = getClass().getResource("/fxImplementation/Sample.fxml");
            if (url == null) {
                throw new RuntimeException("FXML file not found: Sample.fxml");
            }
            FXMLLoader loader = new FXMLLoader(url);
            BorderPane root = loader.load();
            SampleController controller = loader.getController();
            Scene scene = new Scene(root, 700, 500);
            primaryStage.setScene(scene);
            primaryStage.setTitle("JavaFX and Selenium");
            primaryStage.show();

            primaryStage.setOnCloseRequest(event -> controller.shutdown());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
