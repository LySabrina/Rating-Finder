package com.example.ratingfinder.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.Map;

public class TrustedReviewScrape {
    private static String BASE_URL = "https://www.trustedreviews.com/reviews/iphone-x-review";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36";
    private static Map<String, String> HTTP_HEADERS = new HashMap<>(){{put("Accept-Language", "*");put("Referer", "https://www.whathifi.com/us");}};

    public static void crawl(String productName){
        try{
            Document doc = Jsoup.connect(BASE_URL).userAgent(USER_AGENT).headers(HTTP_HEADERS).get();
            System.out.println(doc.toString());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String args[]){
        TrustedReviewScrape.crawl("Test");
    }

}
