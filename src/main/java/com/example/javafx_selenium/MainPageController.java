package com.example.javafx_selenium;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

public class MainPageController {

    @FXML
    private AnchorPane contentPane;

    @FXML
    private void initialize() {
        // Optional: Initialize any default setup here
    }

    @FXML
    private void handleStart() {
        System.out.println("Start button clicked!");
        // Implement your start logic here
    }

    @FXML
    private void MassFollowAction() {
        System.out.println("Mass-Follow button clicked!");

        try {
            // Load MassFollow.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxImplementation/MassFollow.fxml"));
            Pane view = loader.load();

            // Set loaded view to contentPane
            contentPane.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
