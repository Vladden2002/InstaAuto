package scripts;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MassFollow {
    private WebDriver driver;
    private List<String> followedAccounts;
    private int followDuration; 
    private int interval; 
    private int maxAccounts; 

    public MassFollow(int followDuration, int interval, int maxAccounts) {
        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);
        followedAccounts = new ArrayList<>();
        this.followDuration = followDuration;
        this.interval = interval;
        this.maxAccounts = maxAccounts;
    }

    
    public List<String> searchAccountsByCategory(String category) {
        List<String> accounts = new ArrayList<>();
        try {
            driver.get("https://www.instagram.com/explore/tags/" + category + "/");
            Thread.sleep(2000);

            List<WebElement> accountLinks = driver.findElements(By.xpath("//a[contains(@href, '/p/')]"));
            for (WebElement link : accountLinks) {
                link.click();
                Thread.sleep(2000);

                WebElement userLink = driver.findElement(By.xpath("//a[@class='_a6hd']"));
                if (userLink != null) {
                    String username = userLink.getAttribute("href").split("/")[3];
                    if (isValidAccount(username)) {
                        accounts.add(username);
                        System.out.println("Found valid user: " + username);
                    }
                }

                driver.navigate().back();
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accounts;
    }

    
    private boolean isValidAccount(String username) {
        try {
            driver.get("https://www.instagram.com/" + username + "/");
            Thread.sleep(2000);

            WebElement followersElement = driver.findElement(By.xpath("//a[contains(@href, '/followers/')]/span"));
            int followersCount = Integer.parseInt(followersElement.getAttribute("title").replace(",", ""));
            if (followersCount < 100) {
                System.out.println(username + " has less than 100 followers.");
                return false;
            }
            WebElement postsElement = driver.findElement(By.xpath("//span[contains(text(), ' posts')]"));
            int postsCount = Integer.parseInt(postsElement.getText().split(" ")[0].replace(",", ""));
            if (postsCount < 10) {
                System.out.println(username + " is a new account with less than 10 posts.");
                return false;
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

   
    public void followUsers(List<String> usersToFollow) {
        long endTime = System.currentTimeMillis() + followDuration * 60 * 1000; 
        int followedCount = 0;

        try {
            for (String user : usersToFollow) {
                if (System.currentTimeMillis() > endTime || followedCount >= maxAccounts) {
                    break;
                }

                driver.get("https://www.instagram.com/" + user + "/");
                Thread.sleep(2000);

                WebElement followButton = driver.findElement(By.xpath("//button[text()='Follow']"));
                if (followButton != null) {
                    followButton.click();
                    followedAccounts.add(user);
                    followedCount++;
                    System.out.println("Followed " + user);
                } else {
                    System.out.println("Already following " + user + " or follow button not found.");
                }

                Thread.sleep(interval * 1000); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void saveFollowedAccounts(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String account : followedAccounts) {
                writer.write(account);
                writer.newLine();
            }
            System.out.println("Followed accounts saved to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
    	MassFollow massFollow = new MassFollow(30, 10, 50);

        String category = "photography";
        List<String> usersToFollow = massFollow.searchAccountsByCategory(category);

        massFollow.followUsers(usersToFollow);
        massFollow.saveFollowedAccounts("followed_accounts.txt");
        massFollow.driver.quit();
    }
}
