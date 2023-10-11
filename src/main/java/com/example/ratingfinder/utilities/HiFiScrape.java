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

    public static HashMap<String, String> scrape(Document reviewPage){

        HashMap<String, String> categoryText= new HashMap<String, String>();
        //scraping the pros and cons
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

//        System.out.println("PROS: " + proPoints.toString());
//        System.out.println("CONS: " + consPoints.toString());

        Element article = reviewPage.selectFirst("div#article-body");
        Elements h2s = article.select("h2");



        //scraping the text for each section in the article
        for(Element h2: h2s){
            if(h2.attr("id").equals("price") || h2.attr("id").equals("verdict")){
                continue;
            }

            System.out.println("H2-" + h2.text());
            StringBuilder stringBuilder = new StringBuilder();
            Element current = h2.nextElementSibling();

            while(current != null && !current.tagName().equals("h2") ){
                if(current.tagName().equals("p")){
                    System.out.println(current.text());
                    stringBuilder.append(current.text());
                }
                current = current.nextElementSibling();
            }

            categoryText.put(h2.attr("id"), stringBuilder.toString());
        }
        return categoryText;

    }

    /**
     * Crawls through the website and finds the link with the specfied product name
     * @return
     */
    public static Document crawl(String productName){
        try{
            Document doc = Jsoup.connect(BASE_URL).userAgent(USER_AGENT).headers(HTTP_HEADERS).data("searchTerm", productName).get();
//            System.out.println(doc.toString());
//            System.out.println("LOCATION: " + doc.location());
            Elements articleNames = doc.select(".article-name");
//            System.out.println("ARTICLE NAME: " + articleNames.toString());
            boolean reviewFound = false;
            String link = "";


            for(Element e: articleNames){
                if(e.text().toLowerCase().contains(productName.toLowerCase()) && e.text().toLowerCase().contains("review")){
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

    public static HashMap<String, String> getReviews(String productName){
        Document doc = crawl(productName);
        if(doc == null){

            return null;
        }
        else{
            HashMap<String, String> categoryText = scrape(doc);
            return  categoryText;
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
