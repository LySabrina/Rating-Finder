package com.example.ratingfinder.utilities;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HiFiScrape {

    private static String BASE_URL = "https://www.whathifi.com/us/search";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36";
    private static Map<String, String> HTTP_HEADERS = new HashMap<>(){{put("Accept-Language", "*");put("Referer", "https://www.whathifi.com/us");}};

    public static void scrape(Document reviewPage){

        Element pros_container = reviewPage.selectFirst(".pretty-verdict__pros").child(1);
        Elements pros = pros_container.getElementsByTag("p");
        List<String> proPoints = new ArrayList<>();

        for(Element e: pros){
            proPoints.add(e.text());
        }

        Element cons_container = reviewPage.selectFirst(".pretty-verdict__cons").child(1);
        Elements cons = cons_container.getElementsByTag("p");
        List<String> consPoints = new ArrayList<>();
        for(Element e : cons){
            consPoints.add(e.text());
        }

        System.out.println("PROS: " + proPoints.toString());
        System.out.println("CONS: " + consPoints.toString());

    }

    /**
     * Crawls through the website and finds the link with the specfied product name
     * @return
     */
    public static Document crawl(String productName){
        try{
            Document doc = Jsoup.connect(BASE_URL).userAgent(USER_AGENT).headers(HTTP_HEADERS).data("searchTerm", productName).get();
            System.out.println(doc.toString());
            System.out.println("LOCATION: " + doc.location());
            Elements articleNames = doc.select(".article-name");
            System.out.println("ARTICLE NAME: " + articleNames.toString());
            boolean reviewFound = false;
            String link = "";


            for(Element e: articleNames){

                if(e.text().toLowerCase().contains(productName.toLowerCase())){
                    if(e.text().contains("review")){
                        Element anchor = e.parent().parent().parent().parent();
                        link = anchor.attributes().get("href");
                        System.out.println("LINK: " + link);
                        reviewFound = true;
                        break;
                    }
                }

            }

            if(reviewFound){
                Document reviewPage = Jsoup.connect(link).userAgent(USER_AGENT).header("Accept-Language", "*").header("Referer", doc.location()).get();
                return reviewPage;
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void scrape(String productName){
        try{
            Document doc = Jsoup.connect(BASE_URL).userAgent(USER_AGENT).headers(HTTP_HEADERS).data("searchTerm", productName).get();
            System.out.println("LOCATION: " + doc.location());
            Elements articleNames = doc.select(".article-name");
            boolean reviewFound = false;
            String link = "";

            for(Element e: articleNames){
                if(e.text().contains("review")){
                    Element anchor = e.parent().parent().parent().parent();
                    link = anchor.attributes().get("href");
                    reviewFound = true;
                }
            }

            if(reviewFound){
                Document reviewPage = Jsoup.connect(link).userAgent(USER_AGENT).header("Accept-Language", "*").header("Referer", doc.location()).get();
                Element pros_container = reviewPage.selectFirst(".pretty-verdict__pros").child(1);
                Elements pros = pros_container.getElementsByTag("p");
                List<String> proPoints = new ArrayList<>();

                for(Element e: pros){
                    proPoints.add(e.text());
                }

                Element cons_container = reviewPage.selectFirst(".pretty-verdict__cons").child(1);
                Elements cons = cons_container.getElementsByTag("p");
                List<String> consPoints = new ArrayList<>();
                for(Element e : cons){
                    consPoints.add(e.text());
                }

                System.out.println(consPoints.toString());
            }
            else{
                System.out.println("Could not find product");
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //TESTER
    public static void main(String[] args){
        Scanner s = new Scanner(System.in);
        System.out.println("ENTER PRODUCT NAME");
        while(s.hasNext()){
            String input = s.nextLine();
            if(input.equals("")){
                System.out.println("Empty input");
            }
            else{
                Document doc = crawl(input);
                if(doc != null){
                    scrape(doc);
                }
            }
            System.out.println("Enter product name again");
        }

    }
}
