package scripts;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.util.List;

public class MassMessage {
    private WebDriver driver;

    public MassMessage() {
        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);
    }

    public void sendMessageToUsers(List<String> usersToMessage, String message) {
        try {
            for (String user : usersToMessage) {
                driver.get("https://www.instagram.com/" + user + "/");
                Thread.sleep(2000);

                WebElement messageButton = driver.findElement(By.xpath("//div[@role='button']"));
                if (messageButton != null) {
                    messageButton.click();
                } else {
                    System.out.println("Message button not found for " + user);
                    continue;
                }

                Thread.sleep(2000);

                WebElement messageInput = driver.findElement(By.xpath("//div[@contenteditable='true']"));
                if (messageInput != null) {
                    messageInput.sendKeys(message);
                    Thread.sleep(1000);
                    messageInput.sendKeys("\n");
                    System.out.println("Message sent to " + user);
                } else {
                    System.out.println("Message input not found for " + user);
                }

                Thread.sleep(2000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }

    public static void main(String[] args) {
    		///-///
    }
}

