package com.example.javafx_selenium;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class WebDriverManager {

    private static Map<String, WebDriver> drivers = new HashMap<>();
    private static Map<String, BrowserMobProxy> proxies = new HashMap<>();

    private WebDriverManager() {
        // Private constructor to prevent instantiation
    }

    public static synchronized WebDriver getDriver(String userDataDir, String proxyAddress) {
        System.out.println("Attempting to get WebDriver for user data directory: " + userDataDir);
        
        if (!drivers.containsKey(userDataDir)) {
            System.out.println("No existing WebDriver found. Creating a new one.");
            try {
                System.setProperty("webdriver.chrome.driver", "C:/Users/CENTOS-1/Desktop/InstaAuto-main/InstaAuto-main/chromedriver.exe");
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--remote-allow-origins=*");
                options.addArguments("disable-infobars");
                options.addArguments("--disable-extensions");
                options.addArguments("--start-maximized");
                options.addArguments("--disable-popup-blocking");
                options.addArguments("--disable-notifications");
                options.addArguments("--disable-features=EnableAutomaticSignIn");
                options.addArguments("user-data-dir=" + userDataDir);
                options.addArguments("--incognito");

                if (proxyAddress != null && !proxyAddress.isEmpty()) {
                    System.out.println("Proxy address provided: " + proxyAddress);
                    String[] proxyParts = proxyAddress.split(":");
                    String proxyHost = proxyParts[0];
                    int proxyPort = Integer.parseInt(proxyParts[1]);

                    System.out.println("Configuring proxy with host: " + proxyHost + " and port: " + proxyPort);
                    
                    BrowserMobProxy proxy = new BrowserMobProxyServer();
                    proxy.start(0);
                    proxies.put(userDataDir, proxy);

                    Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
                    seleniumProxy.setHttpProxy(proxyHost + ":" + proxyPort);
                    seleniumProxy.setSslProxy(proxyHost + ":" + proxyPort);

                    System.out.println("Proxy configured for HTTP and HTTPS: " + proxyHost + ":" + proxyPort);
                    
                    options.setProxy(seleniumProxy);
                } else {
                    System.out.println("No proxy address provided.");
                }

                WebDriver driver = new ChromeDriver(options);
                drivers.put(userDataDir, driver);
                System.out.println("WebDriver instance created and stored.");
            } catch (Exception e) {
                System.err.println("Error while creating WebDriver instance: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("Reusing existing WebDriver instance.");
        }
        return drivers.get(userDataDir);
    }

    public static synchronized void quitDriver(String userDataDir) {
        System.out.println("Attempting to quit WebDriver for user data directory: " + userDataDir);
        
        if (drivers.containsKey(userDataDir)) {
            try {
                drivers.get(userDataDir).quit();
                drivers.remove(userDataDir);
                System.out.println("WebDriver instance quit and removed.");

                if (proxies.containsKey(userDataDir)) {
                    proxies.get(userDataDir).stop();
                    proxies.remove(userDataDir);
                    System.out.println("Proxy stopped and removed.");
                }
            } catch (Exception e) {
                System.err.println("Error while quitting WebDriver or stopping proxy: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No WebDriver instance found for user data directory: " + userDataDir);
        }
    }
}
