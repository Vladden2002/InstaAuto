package com.example.javafx_selenium;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountManager {

    private static AccountManager instance;
    private List<Account> accounts;

    private AccountManager() {
        accounts = new ArrayList<>();
        loadAccountsFromDatabase();
    }

    public static AccountManager getInstance() {
        if (instance == null) {
            instance = new AccountManager();
        }
        return instance;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void addAccount(Account account) {
        if (doesUsernameExist(account.getUsername())) {
            System.out.println("Username already exists. Please choose a different username.");
            return;
        }
        accounts.add(account);
        saveAccountToDatabase(account);
    }

    public void updateAccount(Account account) {
        String sql = "UPDATE accounts SET password = ?, proxy = ? WHERE username = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, account.getPassword());
            preparedStatement.setString(2, account.getProxy());
            preparedStatement.setString(3, account.getUsername());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteAccount(Account account) {
        String sql = "DELETE FROM accounts WHERE username = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, account.getUsername());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        accounts.remove(account);
    }

    private void loadAccountsFromDatabase() {
        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM accounts")) {

            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String proxy = resultSet.getString("proxy");
                accounts.add(new Account(username, password, proxy));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveAccountToDatabase(Account account) {
        String sql = "INSERT INTO accounts (username, password, proxy) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, account.getUsername());
            preparedStatement.setString(2, account.getPassword());
            preparedStatement.setString(3, account.getProxy());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            if (e instanceof org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException) {
                System.out.println("Username already exists in the database. Please choose a different username.");
            } else {
                e.printStackTrace();
            }
        }
    }

    private boolean doesUsernameExist(String username) {
        String sql = "SELECT COUNT(*) FROM accounts WHERE username = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
