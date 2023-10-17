package com.example.ratingfinder.webscraping;

import com.example.ratingfinder.models.Product;
import com.example.ratingfinder.utilities.HiFiScrape;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GooglePhones {
    private static final String USER_AGENT =  "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36";

    private static Map<String, String> HTTP_HEADERS = new HashMap<>(){{put("Accept-Language", "*");put("Referer", "https:google.com");}};

    public void crawl(){
        System.setProperty("webdriver.chrome.driver", "src/main/resources/jars/chromedriver");
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.bestbuy.com/site/searchpage.jsp?_dyncharset=UTF-8&browsedCategory=pcmcat1625163553254&id=pcat17071&iht=n&ks=960&list=y&qp=brand_facet%3DBrand~Google%5Ecarriercompatibilitysv_facet%3DCarrier%20Compatibility~Unlocked&sc=Global&st=pcmcat1625163553254_categoryid%24abcat0800000&type=page&usc=All%20Categories");
//        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        WebElement button = driver.findElement(new By.ByCssSelector("[data-track='Model Family - Show More']"));
        button.click();
        List<Product> products = new ArrayList<Product>();
        String pageSource = driver.getPageSource();
        WebElement m = driver.findElement(new By.ByCssSelector("fieldset[name='Model Family']"));
        WebElement ul = m.findElement(new By.ByXPath("./ul"));
        List<WebElement> li = ul.findElements(new By.ByCssSelector("li"));

        for(int i = 0;i < li.size()-1; i++){
            List<WebElement> children = li.get(i).findElements(new By.ByCssSelector("*"));
            WebElement c1= children.get(0);
            WebElement c2 = children.get(1);

            String productName = c2.getText();

            c1.click();
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            String pageSoruces = driver.getPageSource();
            Document doc = Jsoup.parse(pageSoruces);
            scrape(doc);
        }

        driver.close();
    }

    public void scrape(Document doc){
        Element ol = doc.selectFirst("ol.sku-item-list");
        Element list = ol.selectFirst(".sku-item");
        System.out.println(list);




    }
    public static void main(String[] args){
        GooglePhones phones = new GooglePhones();
        phones.crawl();
    }
}
