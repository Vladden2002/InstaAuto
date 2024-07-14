package com.example.javafx_selenium;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Random;

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

    @FXML
    private void initialize() {
        driver = WebDriverManager.getDriver();
        ObservableList<String> categories = FXCollections.observableArrayList(
                "Fashion", "Food", "Travel", "Fitness"
            );
        categoriesComboBox.setItems(categories);
    }

    @FXML
    private void handleStart() {
        if (autoSetup.isSelected()) {
            MaxAccounts.setText("5");
            FollowDuration.setText("60");
            try {
                int maxAccounts = Integer.parseInt(MaxAccounts.getText());
                int followDuration = Integer.parseInt(FollowDuration.getText());
                startAutoMode(maxAccounts, followDuration);
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
                startAutoMode(maxAccounts, followDuration);
            } catch (NumberFormatException e) {
                System.out.println("Please enter valid numbers for Max Accounts and Follow Duration.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if(AccountInformation.isSelected()){
        	if (!autoSetup.isSelected() && !advancedSetup.isSelected()) {
                System.out.println("Please select either Auto or Advanced setup.");
                if(advancedSetup.isSelected()) {
                	if (MaxAccounts.getText().isEmpty() || FollowDuration.getText().isEmpty()) {
                        System.out.println("Please enter Max Accounts and Follow Duration.");
                        return;
                    }
                }
                try {
                    int maxAccounts = Integer.parseInt(MaxAccounts.getText());
                    int followDuration = Integer.parseInt(FollowDuration.getText());
                    startAutoMode(maxAccounts, followDuration);
                } catch (NumberFormatException e) {
                    System.out.println("Please enter valid numbers for Max Accounts and Follow Duration.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }else {
            System.out.println("Please select at leaste one setup.");
        }
    }

    private void startAutoMode(int maxAccounts, int followDuration) throws InterruptedException {
        new Thread(() -> {
            try {
            	if(AccountInformation.isSelected()) {
            		navigateToExploreCategories();
            	}else {
            		navigateToExplore();
            	}
                followFromExplore(maxAccounts, followDuration);
            } catch (InterruptedException e) {
                System.out.println("Thread interrupted: " + e.getMessage());
            } finally {
                if (driver != null) {
                    WebDriverManager.quitDriver();
                }
            }
        }).start();
    }
    
    private void navigateToExploreCategories() throws InterruptedException {
        String selectedCategory = categoriesComboBox.getValue();
        if (selectedCategory != null && !selectedCategory.isEmpty()) {
            String exploreURL = INSTAGRAM_URL + "explore/tags/" + selectedCategory + "/";
            driver.get(exploreURL);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(getExplorePostXpath())));
        } else {
            System.out.println("No category selected.");
        }
    }


    private void navigateToExplore() throws InterruptedException {
        driver.get(INSTAGRAM_URL + "explore/");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(getExplorePostXpath())));
    }

    private void followFromExplore(int maxAccountsToFollow, int followDuration) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

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
                WebElement followButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(@class, '_acan') and contains(@class, '_acap') and contains(@class, '_acas') and contains(@class, '_aj1-') and contains(@class, '_ap30')]")));
                followButton.click();
                System.out.println("Followed a user.");

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
    }

    private String getExplorePostXpath() {
        return "//a[contains(@href, '/p/')]";
    }
}
	