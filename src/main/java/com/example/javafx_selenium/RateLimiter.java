package com.example.javafx_selenium;

import java.util.concurrent.TimeUnit;

public class RateLimiter {
    private static final long REQUEST_INTERVAL = 5000; // 5 seconds
    private static long lastRequestTime = 0;

    public static void waitIfNeeded() {
        long currentTime = System.currentTimeMillis();
        if (lastRequestTime != 0 && (currentTime - lastRequestTime < REQUEST_INTERVAL)) {
            try {
                TimeUnit.MILLISECONDS.sleep(REQUEST_INTERVAL - (currentTime - lastRequestTime));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        lastRequestTime = currentTime;
    }
}
