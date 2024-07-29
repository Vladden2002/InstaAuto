package com.example.javafx_selenium;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class SampleController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private CheckBox termsCheckbox;

    @FXML
    private Button testButton;  // New button for testing

    private AccountManager accountManager = AccountManager.getInstance();

    @FXML
    private void initialize() {
        // Initialize any necessary UI components or bindings
    }

    @FXML
    private void handleLogin() {
        if (!termsCheckbox.isSelected()) {
            // Handle case where terms and conditions are not accepted
            System.out.println("Please accept the terms and conditions.");
            return;
        }

        String username = usernameField.getText();
        String password = passwordField.getText();
        String proxy = "176.100.146.206:62018:hpkTdez8:A4vVs3Tk";

        // Add account to AccountManager
        accountManager.addAccount(new Account(username, password, proxy));
        System.out.println("Account added: " + username);

        // Change scene to MainPage.fxml
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxImplementation/MainPage.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) this.loginButton.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handleTestButtonAction() {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxImplementation/MainPage.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) this.testButton.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void shutdown() {
    	DatabaseManager.shutdownDatabase();
    }
}
