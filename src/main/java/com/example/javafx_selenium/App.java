package com.example.javafx_selenium;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            DatabaseManager.initializeDatabase();
            DatabaseManager.startDatabaseConsole();

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

            primaryStage.setOnCloseRequest(event -> {
                controller.shutdown();
                DatabaseManager.shutdownDatabase();
            });

            // Add a shutdown hook to ensure the database is closed when the application exits
            Runtime.getRuntime().addShutdownHook(new Thread(() -> DatabaseManager.shutdownDatabase()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
