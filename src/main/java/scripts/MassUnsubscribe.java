package scripts;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.util.List;

public class MassUnsubscribe {
    private WebDriver driver;

    public MassUnsubscribe() {
        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);
    }

    public void unfollowUsers(List<String> usersToUnfollow) {
        try {
            for (String user : usersToUnfollow) {
                driver.get("https://www.instagram.com/" + user + "/");
                Thread.sleep(2000);

                WebElement unfollowButton = driver.findElement(By.xpath("//button[text()='Following' or text()='Requested']"));
                if (unfollowButton != null) {
                    unfollowButton.click();

                    Thread.sleep(2000);

                    WebElement confirmButton = driver.findElement(By.xpath("//button[text()='Unfollow']"));
                    if (confirmButton != null) {
                        confirmButton.click();
                        System.out.println("Unfollowed " + user);
                    } else {
                        System.out.println("Second unfollow button not found for " + user);
                    }
                } else {
                    System.out.println("Not following " + user + " or unfollow button not found.");
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
        MassUnsubscribe massUnsubscribe = new MassUnsubscribe();
        List<String> usersToUnfollow = List.of("user1", "user2", "user3");
        massUnsubscribe.unfollowUsers(usersToUnfollow);
    }
}
