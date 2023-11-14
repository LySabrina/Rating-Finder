//import com.example.ratingfinder.service.RTINGsScrape;
//import jdk.swing.interop.SwingInterOpUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.List;
public class MainTester extends SpringBootServletInitializer {

    public static void main(String args[]){
//        System.setProperty("webdriver.chrome.driver","/Users/sabrinaly/Desktop/jars/chromedriver" );
//        WebDriver driver = new ChromeDriver();
//        driver.manage().window().maximize();
//        driver.get("https://www.rtings.com/search?q=sony%20wf-1000xm4");
//        Document doc = Jsoup.parse(driver.getPageSource());
//        List<WebElement> elementList = driver.findElements(By.className(".searchbar_results-result"));
//        System.out.println(elementList.toString());
        try{
            Document doc = Jsoup.connect("https://www.trustedreviews.com/reviews/sony-wf-1000xm5").get();
            System.out.println(doc.toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }



    }


}
