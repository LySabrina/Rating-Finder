package com.example.ratingfinder.webscraping;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SamsungPhones {
    private static final String USER_AGENT =  "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36";

    private static Map<String, String> HTTP_HEADERS = new HashMap<>(){{put("Accept-Language", "*");put("Referer", "https:google.com");}};

    public static void crawl(){
        try{
            //Using Selenium Driver because you will need to load JavaScript first to get all the products
            System.setProperty("webdriver.chrome.driver", "src/main/resources/jars/chromedriver");
            WebDriver driver = new ChromeDriver();
            driver.get("https://www.samsung.com/us/mobile/phones/all-phones/");
            driver.manage().timeouts().implicitlyWait(12, TimeUnit.SECONDS);

            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.scrollTo(0,document.body.scrollHeight)");


            String pageSource = driver.getPageSource();
            Document doc = Jsoup.parse(pageSource);


            Element phone_container = doc.selectFirst("div#eppStore").nextElementSibling().child(1);


            Elements phone_cards = phone_container.select("[class^=ProductCard-root]");
//            System.out.println(phone_cards);
            System.out.println("LENGTH: " + phone_cards.size());
            for(Element phone_card : phone_cards){

                Element div = phone_card.child(1);
                System.out.println(div);

                Element image = div.child(0);
                Element text_info = div.child(1);

                String name = text_info.child(1).child(0).text();

                System.out.println(name);
            }

            driver.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        try{
            Document doc = Jsoup.connect("https://www.bestbuy.com/site/samsung-galaxy/all-samsung-galaxy-phones/pcmcat1661803373461.c?id=pcmcat1661803373461").headers(HTTP_HEADERS).userAgent(USER_AGENT).get();
            System.out.println(doc.toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}
