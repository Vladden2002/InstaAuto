package scripts;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.util.List;

public class MassComment {
    private WebDriver driver;

    public MassComment() {
        ChromeOptions options = new ChromeOptions();
        // Add options if needed (e.g., headless mode)
        driver = new ChromeDriver(options);
    }

    public void commentOnPosts(List<String> usersToComment, String commentText) {
        try {
            for (String user : usersToComment) {
                driver.get("https://www.instagram.com/" + user + "/");
                Thread.sleep(2000);

                List<WebElement> postButtons = driver.findElements(By.xpath("//div[@class='_aagw']"));
                if (postButtons.isEmpty()) {
                    System.out.println(user + " has no posts to comment on.");
                    continue;
                }

                System.out.println("Found " + postButtons.size() + " posts for " + user);

                for (WebElement postButton : postButtons) {
                    Thread.sleep(2000);
                    postButton.click();

                    Thread.sleep(2000);

                    WebElement commentInput = driver.findElement(By.xpath("//textarea[@aria-label='Add a comment…']"));
                    if (commentInput != null) {
                        commentInput.sendKeys(commentText);
                        Thread.sleep(1000);
                        commentInput.sendKeys("\n");
                        System.out.println("Commented \"" + commentText + "\" on a post");
                    } else {
                        System.out.println("Comment input not found");
                    }

                    driver.navigate().back();
                    Thread.sleep(2000);
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
    	////-///
    }
}
