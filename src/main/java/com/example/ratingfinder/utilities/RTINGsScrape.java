package com.example.ratingfinder.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

public class RTINGsScrape{

    private String BASE_URL = "https://www.rtings.com/search";
    private static final String USER_AGENT =  "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36";

    private static Map<String, String> HTTP_HEADERS = new HashMap<>(){{put("Accept-Language", "*");put("Referer", "https:google.com");}};

    public void scrape(Document reviewPage) {

    }


    public static Document crawl(String productName) {

        try{
//            Document doc = Jsoup.connect("https://www.rtings.com/search?q=sony%20wf-1000xm4").userAgent(USER_AGENT).headers(HTTP_HEADERS).get();
//            System.out.println(doc.toString());
//            System.out.println("LOCATION: " + doc.location());
//            Elements articleNames = doc.select(".searchbar_results-result");
//            for(Element e : articleNames){
//                System.out.println(e.toString());
//            }
            Document doc = Jsoup.connect("https://www.soundguys.com/sony-wf-1000xm4-review-53454/").userAgent(USER_AGENT).headers(HTTP_HEADERS).get();
            System.out.println(doc.toString());
            return doc;
        }
        catch (Exception e ){
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args){
        Document doc = RTINGsScrape.crawl("test");
    }
}
