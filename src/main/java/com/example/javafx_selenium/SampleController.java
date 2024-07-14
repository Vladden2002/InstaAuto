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
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;

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

    private WebDriver driver;

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

        // Run WebDriver actions in a separate thread to avoid blocking the UI thread
        new Thread(() -> {
            try {
                System.out.println("Initializing WebDriver...");
                driver = WebDriverManager.getDriver();
                
                System.out.println("Navigating to Instagram login page...");
                driver.get("https://www.instagram.com/accounts/login");

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

                System.out.println("Waiting for username input field...");
                WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
                usernameInput.sendKeys(username);

                System.out.println("Waiting for password input field...");
                WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
                passwordInput.sendKeys(password);

                System.out.println("Waiting for login button to be clickable...");
                WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@type='submit']")));
                loginButton.click();

                System.out.println("Login button clicked. Waiting for navigation...");
                // Optional: Wait for post-login page to load
                wait.until(ExpectedConditions.urlContains("https://www.instagram.com/"));

                System.out.println("Login successful.");

                // Change scene to MainPage.fxml after successful login
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

            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
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
    	WebDriverManager.quitDriver();
    }
}
