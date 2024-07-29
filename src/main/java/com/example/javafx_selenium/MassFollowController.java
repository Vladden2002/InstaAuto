package com.example.javafx_selenium;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MassFollowController {

    @FXML
    private CheckBox autoSetup;

    @FXML
    private CheckBox advancedSetup;
    
    @FXML
    private CheckBox AccountInformation;

    @FXML
    private TextField MaxAccounts;
    
    @FXML
    private ComboBox<String> categoriesComboBox;

    @FXML
    private TextField FollowDuration;


    private WebDriver driver;
    private Random random = new Random();

    private static final String INSTAGRAM_URL = "https://www.instagram.com/";

    private Account selectedAccount;
    
    private ExecutorService executorService;
    
    private FollowedAccountManager followmanager = FollowedAccountManager.getInstance();

    @FXML
    private void initialize() {
        ObservableList<String> categories = FXCollections.observableArrayList(
                "Fashion", "Food", "Travel", "Fitness"
            );
        categoriesComboBox.setItems(categories);
        executorService = Executors.newFixedThreadPool(4);
    }

    public void setSelectedAccount(Account account) {
        this.selectedAccount = account;
    }

    @FXML
    private void handleStart() {
        String username = selectedAccount.getUsername();
        String password = selectedAccount.getPassword();
        String proxy = selectedAccount.getProxy();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Please enter both username and password.");
            return;
        }
        
        String userDataDir = "C:/Users/CENTOS-1/Desktop/profiles/" + username;

        // Run WebDriver actions in a separate thread to avoid blocking the UI thread
        executorService.submit(() -> {
        	WebDriver driver = WebDriverManager.getDriver(userDataDir, proxy);
            try {
                loginToInstagram(driver, username, password);
                if (autoSetup.isSelected()) {
                    MaxAccounts.setText("5");
                    FollowDuration.setText("60");
                    try {
                        int maxAccounts = Integer.parseInt(MaxAccounts.getText());
                        int followDuration = Integer.parseInt(FollowDuration.getText());
                        startAutoMode(driver, maxAccounts, followDuration, userDataDir);
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter valid numbers for Max Accounts and Follow Duration.");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (advancedSetup.isSelected()) {
                    if (MaxAccounts.getText().isEmpty() || FollowDuration.getText().isEmpty()) {
                        System.out.println("Please enter Max Accounts and Follow Duration.");
                        return;
                    }

                    try {
                        int maxAccounts = Integer.parseInt(MaxAccounts.getText());
                        int followDuration = Integer.parseInt(FollowDuration.getText());
                        startAutoMode(driver, maxAccounts, followDuration, userDataDir);
                    } catch (NumberFormatException e) {
                        System.out.println("Please enter valid numbers for Max Accounts and Follow Duration.");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (AccountInformation.isSelected()) {
                    if (!autoSetup.isSelected() && !advancedSetup.isSelected()) {
                        System.out.println("Please select either Auto or Advanced setup.");
                        if (advancedSetup.isSelected()) {
                            if (MaxAccounts.getText().isEmpty() || FollowDuration.getText().isEmpty()) {
                                System.out.println("Please enter Max Accounts and Follow Duration.");
                                return;
                            }
                        }
                        try {
                            int maxAccounts = Integer.parseInt(MaxAccounts.getText());
                            int followDuration = Integer.parseInt(FollowDuration.getText());
                            startAutoMode(driver, maxAccounts, followDuration, userDataDir);
                        } catch (NumberFormatException e) {
                            System.out.println("Please enter valid numbers for Max Accounts and Follow Duration.");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    System.out.println("Please select at least one setup.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private void loginToInstagram(WebDriver driver, String username, String password) {
        System.out.println("Chosen account username: " + username);
        System.out.println("Chosen account password: " + password);

        try {
            System.out.println("Navigating to Instagram login page...");
            driver.get("https://www.instagram.com");

            WebDriverWait wait = new WebDriverWait(driver, 10);

            // Accept cookies if prompt is present
            try {
                WebElement acceptCookiesButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Allow all cookies']")));
                acceptCookiesButton.click();
                System.out.println("Accepted cookies pop-up.");
            } catch (Exception e) {
                System.out.println("No cookies pop-up to handle.");
            }

            // Enter username
            Thread.sleep(2000);
            System.out.println("Waiting for username input field...");
            WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("username")));
            usernameInput.sendKeys(username);

            // Enter password
            Thread.sleep(2000);
            System.out.println("Waiting for password input field...");
            WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));
            passwordInput.sendKeys(password);

            // Click login button
            Thread.sleep(2000);
            System.out.println("Waiting for login button to be clickable...");
            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@type='submit']")));
            loginButton.click();

            // Check if security code verification is needed
            try {
            	WebElement verificationMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[contains(text(), \"To secure your account, we'll send you a security code to verify your identity.\")]")));
                if (verificationMessage.isDisplayed()) {
                    System.out.println("Verification required. Clicking 'Continue' button.");

                    // Click the 'Continue' button
                    WebElement continueButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(@class, 'x1i10hfl') and @role='button']")));
                    continueButton.click();
                    System.out.println("Clicked 'Continue' button.");

                    // Get verification code from user
                    String verificationCode = showVerificationDialog();
                    if (verificationCode != null && !verificationCode.isEmpty()) {
                        // Locate the security code input field
                        WebElement codeInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("security_code")));
                        codeInput.sendKeys(verificationCode);
                        System.out.println("Entered verification code.");

                        // Click the 'Submit' button
                        WebElement submitButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[contains(@class, 'x1i10hfl') and @role='button']")));
                        submitButton.click();
                        System.out.println("Clicked 'Submit' button.");
                    }
                }
            } catch (Exception e) {
                System.out.println("No verification required or an error occurred: " + e.getMessage());
            }

            // Wait to ensure successful login
            wait.until(ExpectedConditions.urlContains("https://www.instagram.com/"));

            // Verify if logged in by checking the presence of an element on the home page
            try {
                WebElement homeElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[text()='Home']")));
                if (homeElement.isDisplayed()) {
                    System.out.println("Login successful!");
                }
            } catch (Exception e) {
                System.out.println("Login might not have been successful. Please check credentials.");
            }

        } catch (Exception e) {
            System.out.println("An error occurred during login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    private String showVerificationDialog() {
        String[] code = new String[1]; // Array to hold the code because JavaFX doesn't support direct return of values from dialogs
        
        javafx.application.Platform.runLater(() -> {
            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Verification Code");

            VBox dialogVbox = new VBox(20);
            dialogVbox.setAlignment(Pos.CENTER);
            
            Label label = new Label("Enter the verification code sent to your email:");
            TextField textField = new TextField();
            Button submitButton = new Button("Submit");
            
            submitButton.setOnAction(e -> {
                code[0] = textField.getText();
                dialog.close();
            });
            
            dialogVbox.getChildren().addAll(label, textField, submitButton);
            Scene dialogScene = new Scene(dialogVbox, 300, 200);
            dialog.setScene(dialogScene);
            dialog.show();
        });

        // Wait until the user has entered the code
        while (code[0] == null) {
            try {
                Thread.sleep(100); // Polling interval
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return code[0];
    }






    private void startAutoMode(WebDriver driver, int maxAccounts, int followDuration, String userdata) throws InterruptedException {
        new Thread(() -> {
            try {
                if (AccountInformation.isSelected()) {
                    navigateToExploreCategories(driver);
                } else {
                    navigateToExplore(driver);
                }
                followFromExplore(driver, maxAccounts, followDuration);
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted: " + e.getMessage());
            } finally {
                if (driver != null) {
                    WebDriverManager.quitDriver(userdata);
                }
            }
        }).start();
    }

    private void navigateToExploreCategories(WebDriver driver) throws InterruptedException {
        String selectedCategory = categoriesComboBox.getValue();
        if (selectedCategory != null && !selectedCategory.isEmpty()) {
            String exploreURL = INSTAGRAM_URL + "explore/tags/" + selectedCategory + "/";
            driver.get(exploreURL);
            WebDriverWait wait = new WebDriverWait(driver, 10);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(getExplorePostXpath())));
        } else {
            System.out.println("No category selected.");
        }
    }

    private void navigateToExplore(WebDriver driver) throws InterruptedException {
        driver.get(INSTAGRAM_URL + "explore/");
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(getExplorePostXpath())));
    }

    public void followFromExplore(WebDriver driver, int maxAccountsToFollow, int followDuration) throws InterruptedException {
    	WebDriverWait wait = new WebDriverWait(driver, 10);
        List<String> followedUsernames = new ArrayList<>();

        for (int i = 0; i < maxAccountsToFollow; i++) {
            List<WebElement> posts = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.xpath(getExplorePostXpath())));

            if (i >= posts.size()) {
                System.out.println("No more posts to process.");
                break;
            }

            WebElement post = posts.get(i);
            post.click();
            System.out.println("Clicked on post " + (i + 1));
            Thread.sleep(5000); // Wait for the post to load

            WebElement creatorProfile = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@class, '_acan') and contains(@class, '_acao') and @role='link']")));
            creatorProfile.click();
            System.out.println("Navigated to creator profile.");
            Thread.sleep(5000); // Wait for the profile to load

            try {
                // Extract the username
                WebElement usernameElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//header//h2")));
                String username = usernameElement.getText();
                FollowedAccount f = new FollowedAccount(username, selectedAccount);
                followmanager.addFollowedAccount(f);
                followedUsernames.add(username);
                

                // Follow the user
                WebElement followButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@class, '_acan') and contains(@class, '_acap') and contains(@class, '_acas') and contains(@class, '_aj1-') and contains(@class, '_ap30')]")));
                followButton.click();
                System.out.println("Followed user: " + username);

                long startTime = System.currentTimeMillis();
                long currentTime = startTime;
                long elapsedTime = 0;
                while (elapsedTime < followDuration * 1000) {
                    Thread.sleep(1000);
                    currentTime = System.currentTimeMillis();
                    elapsedTime = currentTime - startTime;
                }

            } catch (StaleElementReferenceException e) {
                System.out.println("Stale element exception occurred: " + e.getMessage());
            }

            driver.navigate().back();
            Thread.sleep(2000);

            driver.navigate().back();
            Thread.sleep(2000);
        }

        System.out.println("Follow end");
        System.out.println("Followed usernames: " + String.join(", ", followedUsernames));
    }

    private String getExplorePostXpath() {
        return "//a[contains(@href, '/p/')]";
    }
}