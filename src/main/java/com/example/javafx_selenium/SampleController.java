package com.example.javafx_selenium;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class SampleController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private CheckBox termsCheckbox;

    private WebDriver driver;

    public SampleController() {
        // Set up WebDriver (assumes ChromeDriver is installed and on PATH)
        System.setProperty("webdriver.chrome.driver", "C:/Users/Olga Dombrovan/Desktop/demo/javafx-selenium/chrome-headless-shell.exe");
        driver = new ChromeDriver();
    }

    @FXML
    private void initialize() {
        // Initialize any necessary UI components or bindings
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Navigate to Instagram login page
        driver.get("https://www.instagram.com/accounts/login/");

        // Enter username and password
        WebElement usernameInput = driver.findElement(By.name("username"));
        WebElement passwordInput = driver.findElement(By.name("password"));
        usernameInput.sendKeys(username);
        passwordInput.sendKeys(password);

        // Click login button
        WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit']"));
        loginButton.click();
    }

    public void shutdown() {
        // Close the WebDriver when application exits
        if (driver != null) {
            driver.quit();
        }
    }
}
