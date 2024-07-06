package scripts;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.util.List;

public class MassLike {
    private WebDriver driver;

    public MassLike() {
        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);
    }

    public void likePosts(List<String> usersToLike) {
        try {
            for (String user : usersToLike) {
                driver.get("https://www.instagram.com/" + user + "/");
                Thread.sleep(2000);

                List<WebElement> postButtons = driver.findElements(By.xpath("//div[@class='_aagw']"));
                if (postButtons.isEmpty()) {
                    System.out.println(user + " has no posts to like.");
                    continue;
                }

                System.out.println("Found " + postButtons.size() + " posts for " + user);

                for (WebElement postButton : postButtons) {
                    Thread.sleep(2000);
                    postButton.click();

                    Thread.sleep(2000);

                    WebElement likeButton = driver.findElement(By.xpath("//svg[@aria-label='Like']"));
                    if (likeButton != null) {
                        likeButton.click();
                        System.out.println("Liked post");
                    } else {
                        System.out.println("Post already liked or like button not found");
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
    		///-///
    }
}
