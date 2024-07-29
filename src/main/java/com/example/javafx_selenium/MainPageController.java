package com.example.javafx_selenium;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

public class MainPageController {

	@FXML
    private AnchorPane contentPane;

    @FXML
    private ComboBox<Account> AccountsComboBox;

    @FXML
    private TextField UsernameField;

    @FXML
    private TextField PasswordField;
    
    @FXML
    private TextField ProxyField;

    @FXML
    private Button AddButton;

    @FXML
    private Button AddAccount;
    
    private AccountManager accountManager = AccountManager.getInstance();

    @FXML
    private void initialize() {
    	populateAccountsComboBox();
        UsernameField.setVisible(false);
        PasswordField.setVisible(false);
        AddButton.setVisible(false);
        ProxyField.setVisible(false);
    }
    
    private void populateAccountsComboBox() {
        AccountsComboBox.getItems().setAll(accountManager.getAccounts());
    }

    @FXML
    private void AddAccountButton() {
        UsernameField.setVisible(true);
        PasswordField.setVisible(true);
        AddButton.setVisible(true);
        ProxyField.setVisible(true);
    }

    @FXML
    private void AddButtonAction() {
        String username = UsernameField.getText();
        String password = PasswordField.getText();
        String proxy = ProxyField.getText();
        
        if (!username.isEmpty() && !password.isEmpty()) {
            accountManager.addAccount(new Account(username, password, proxy));
            populateAccountsComboBox();
            
            UsernameField.clear();
            PasswordField.clear();
            ProxyField.clear();
            UsernameField.setVisible(false);
            PasswordField.setVisible(false);
            AddButton.setVisible(false);
            ProxyField.setVisible(false);
        } else {
            System.out.println("Please enter both username and password.");
        }
    }

    @FXML
    private void AccountsComboBoxAction() {
        Account selectedAccount = AccountsComboBox.getValue();
        // Handle the selection of an account from the ComboBox
        System.out.println("Selected account: " + selectedAccount.getUsername());
    }

    @FXML
    private void MassFollowAction() throws IOException {
        System.out.println("Mass-Follow button clicked!");
        Account selectedAccount = AccountsComboBox.getValue();
        
        if (selectedAccount != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxImplementation/MassFollow.fxml"));
            Pane view = loader.load();

            // Get the controller instance from the FXMLLoader
            MassFollowController massFollowController = loader.getController();
            System.out.println("chosen account - " + selectedAccount);

            // Set the selected account in the controller instance
            massFollowController.setSelectedAccount(selectedAccount);

            // Navigate to the mass follow page
            contentPane.getChildren().setAll(view);
        } else {
            System.out.println("Please select an account to proceed.");
        }
    }

}
