package start;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utilities.BrowserFactory;

import java.util.ArrayList;
import java.util.Scanner;

public class SearchAmazon {

    public static void main(String[] args) throws InterruptedException {
        Scanner scan = new Scanner(System.in);
        System.out.println("Please type what you want to search in Amazon.com");
        String searchTerm = scan.nextLine();

        WebDriver driver = BrowserFactory.getDriver("firefox");
        driver.get("https://www.amazon.com");
        WebElement searchBox = driver.findElement(By.id("twotabsearchtextbox"));
        searchBox.sendKeys(searchTerm);
        //instead of clicking on search button, hitting enter key this way:
        searchBox.sendKeys(Keys.ENTER);
        Thread.sleep(2000);
        if (driver.findElements(By.xpath("//span[starts-with(text(),'No results for')]")).size() == 1) {
            System.out.println("No results for '" + searchTerm + "'");
            driver.quit();
            return;
        }
        int pageCount = 0;
        int totalResults = 0;
        int whichResult = 0;

        while (true) {

            WebElement nextPageBtn = driver.findElement(By.xpath("//ul[contains(@class,'a-pagination')]//li[contains(@class,'a-last')]"));

            String xpathToResults = "//div[contains(@class,'s-main-slot s-result-list s-search-results sg-row')]//*[starts-with(@data-cel-widget,'search_result')][@data-component-type='s-search-result']//span//a//div//img";

            ArrayList<WebElement> results = (ArrayList<WebElement>) driver.findElements(By.xpath(xpathToResults));
            // I wanted to calculate the number of results without taking it from Amazon
            totalResults += results.size();

            pageCount++;

            ArrayList<WebElement> linkElements = (ArrayList<WebElement>) driver.findElements(By.xpath(xpathToResults + "/../.."));

            for (int j = 0; j < results.size(); j++) {
                WebElement eachResult = results.get(j);
                //every image element has an attribute named "alt" which has an info about product or sth.
                String resultTitle = eachResult.getAttribute("alt");

                // To get the link of the result, I picked up the parent of parent element of eachResult element by using dots below
                WebElement eachLinkElement = linkElements.get(j);
                System.out.println((whichResult+1)+". "+ resultTitle);
                String urlToResult = eachLinkElement.getAttribute("href");
                System.out.println("Link: " + urlToResult);
                whichResult++;
            }
            if (nextPageBtn.getAttribute("class").contains("a-disabled")) {
                //Next is not clickable anymore, so there is no next page, time to break
                break;
            } else {
                nextPageBtn.click();
                // To get nothing wrong with locating element till learning WebDriverWait
                Thread.sleep(5000);
            }
        }
        System.out.println("total results number: " + totalResults);
        System.out.println(pageCount + " pages of results were there in total");
        driver.quit();

    }
}
