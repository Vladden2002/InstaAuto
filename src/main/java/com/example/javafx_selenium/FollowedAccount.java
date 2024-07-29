package com.example.javafx_selenium;

public class FollowedAccount {
    private String username;
    private Account account;

    public FollowedAccount(String username, Account account) {
        this.username = username;
        this.account = account;
    }

    public String getUsername() {
        return username;
    }

    public Account getAccount() {
        return account;
    }
}
