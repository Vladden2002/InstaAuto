package com.example.javafx_selenium;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FollowedAccountManager {

    private static FollowedAccountManager instance;
    private List<FollowedAccount> followedAccounts;

    private FollowedAccountManager() {
        followedAccounts = new ArrayList<>();
        loadFollowedAccountsFromDatabase();
    }
    
    

    public static FollowedAccountManager getInstance() {
        if (instance == null) {
            instance = new FollowedAccountManager();
        }
        return instance;
    }

    public List<FollowedAccount> getFollowedAccounts() {
        return followedAccounts;
    }

    public void addFollowedAccount(FollowedAccount followedAccount) {
        followedAccounts.add(followedAccount);
        saveFollowedAccountToDatabase(followedAccount);
    }

    public void updateFollowedAccount(FollowedAccount followedAccount) {
        String sql = "UPDATE followed_users SET account_username = ? WHERE username = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, followedAccount.getAccount().getUsername());
            preparedStatement.setString(2, followedAccount.getUsername());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteFollowedAccount(FollowedAccount followedAccount) {
        String sql = "DELETE FROM followed_users WHERE username = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, followedAccount.getUsername());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        followedAccounts.remove(followedAccount);
    }

    private void loadFollowedAccountsFromDatabase() {
        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM followed_users")) {

            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String accountUsername = resultSet.getString("account_username");

                Account account = AccountManager.getInstance().getAccounts().stream()
                        .filter(acc -> acc.getUsername().equals(accountUsername))
                        .findFirst()
                        .orElse(null);

                if (account != null) {
                    followedAccounts.add(new FollowedAccount(username, account));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveFollowedAccountToDatabase(FollowedAccount followedAccount) {
        String sql = "INSERT INTO followed_users (username, account_username) VALUES (?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, followedAccount.getUsername());
            preparedStatement.setString(2, followedAccount.getAccount().getUsername());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
