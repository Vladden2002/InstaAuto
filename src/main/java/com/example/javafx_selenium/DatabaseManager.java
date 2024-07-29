package com.example.javafx_selenium;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import org.h2.tools.Server;

public class DatabaseManager {

    private static final String JDBC_URL = "jdbc:h2:~/test;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    private static Server webServer;

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    public static void initializeDatabase() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS accounts (username VARCHAR(255) PRIMARY KEY, password VARCHAR(255))");
            statement.execute("ALTER TABLE accounts ADD COLUMN IF NOT EXISTS proxy VARCHAR(255)");
            statement.execute("CREATE TABLE IF NOT EXISTS followed_users (username VARCHAR(255), account_username VARCHAR(255), FOREIGN KEY (account_username) REFERENCES accounts(username))");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    public static void startDatabaseConsole() {
        try {
            webServer = Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8083").start();
            System.out.println("H2 Database console started at http://localhost:8083");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void shutdownDatabase() {
        if (webServer != null) {
            webServer.stop();
            System.out.println("H2 Database console stopped.");
        }
    }
}
