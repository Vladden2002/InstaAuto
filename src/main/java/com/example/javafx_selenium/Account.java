package com.example.javafx_selenium;

public class Account {
    private String username;
    private String password;
    private String proxy;

    public Account(String username, String password, String proxy) {
        this.username = username;
        this.password = password;
        this.proxy = proxy;
    }

    public String getUsername() {
        return username;
    }
    
    public String getProxy() {
    	return proxy;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return username;
    }
}
