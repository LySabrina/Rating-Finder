package com.example.ratingfinder.utilities;

import com.example.ratingfinder.Repository.ProductRepository;
import com.example.ratingfinder.Repository.ReviewRepository;
import com.example.ratingfinder.models.Product;
import com.example.ratingfinder.models.Review;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class WhatHifi {
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36";
    private static Map<String, String> HTTP_HEADERS = new HashMap<>(){{put("Accept-Language", "*");put("Referer", "https://www.google.com/");}};

    private ProductRepository productRepository;
    private ReviewRepository reviewRepository;
    private ChatGPT chatGPT;

    public WhatHifi(ProductRepository productRepository, ReviewRepository reviewRepository, ChatGPT chatGPT){
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
        this.chatGPT = chatGPT;
    }
    //check if the product name is in the database. If it's get the product id. Make a review and assign it.
    //If the product is not in the database, add it .

    //DO .contains() JUST BECAUSE it might differ slightly
    public void crawl(String startingURL, String type){
        try{
            Document doc = Jsoup.connect(startingURL).headers(HTTP_HEADERS).userAgent(USER_AGENT).get();
            //scrape the first page


            scrapePage(doc, type);
            Element starting = doc.selectFirst(".current-page");
            Elements page_links = starting.nextElementSiblings().select(".pagination-numerical-list-item-link");

            for(Element page_link : page_links){
                System.out.println("ON PAGE - " + page_link.text());
                String link = page_link.attr("href");

                Document linkDoc = Jsoup.connect(link).headers(HTTP_HEADERS).userAgent(USER_AGENT).get();
                scrapePage(linkDoc, type);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Check if the product exists in the database
     * @param doc
     */
    public void scrapePage(Document doc, String type) throws IOException {
        Elements articleReviews = doc.select("[data-page]");

        for(Element articleReview : articleReviews){
            Element article = articleReview.child(0);

            String productName = article.attr("aria-label");
            productName= productName.replaceAll(" review", "");




            System.out.println("PRODUCT NAME - " + productName);
            String brand = productName.substring(0, productName.indexOf(" "));
            String articleLink = article.attr("href");
            System.out.println("ARTICLE LINK - " + articleLink);

            Element img_container = article.child(0).child(0).selectFirst(".image");
            Element img = img_container.child(0).child(0).child(0).child(0).child(1);


            String img_links = img.attr("srcset");
            if(img_links.isEmpty()){
                img_links = img.attr("data-srcset");
            }

            String img_link = img_links.substring(0, img_links.indexOf(" "));
            Connection.Response response = Jsoup.connect(img_link).ignoreContentType(true).execute();
            InputStream inputStream = response.bodyStream();
            byte[] imgBytes = inputStream.readAllBytes();
            System.out.println("IMG_LINK - " + img_link);

            Product p = new Product();
            p.setName(productName);
            p.setBrand(brand);
            p.setType(type);
            p.setImage(imgBytes);
            Document reviewPage = Jsoup.connect(articleLink).headers(HTTP_HEADERS).userAgent(USER_AGENT).get();

            double price = 0;
            if(reviewPage.selectFirst(".testedAtPrice") != null){
                String priceText = reviewPage.selectFirst(".testedAtPrice").text();
                System.out.println(priceText);


                if(priceText.contains("$")){
                    int dollarIndex = priceText.indexOf("$");
                    System.out.println(priceText.substring(dollarIndex));
                    String temp = priceText.substring(dollarIndex);
                    int spaceIndex = priceText.substring(dollarIndex).indexOf(" ");

                    if(spaceIndex != -1){
                        price = Double.parseDouble (temp.substring(1, spaceIndex));
                    }
                    else{
                        price = Double.parseDouble(priceText.substring(dollarIndex+1));
                    }
                }
            }



            System.out.println("PRICE - " + price);
            p.setPrice(price);

            Product savedProduct = productRepository.save(p);
            HashMap<String, String> categoryText = scrapeReviewPage(reviewPage);
            addReview(savedProduct, categoryText);


        }
    }

    public void addReview(Product savedProduct, HashMap<String, String> categoryText){
        Set<String> keys = categoryText.keySet();
        //Creating Review objects that has summarized text by WhatHifi
        for(String category : keys){
            String text = categoryText.get(category);
            String summarizedText = chatGPT.chatGPT ("Summarize this in a few sentences: " + text);
//                System.out.println("SUMMARIZED TEXT = " + summarizedText);
            Review review = new Review();
            review.setCompany("What HiFi");
            review.setCategory(category);
            review.setProduct(savedProduct);
            review.setSummary(summarizedText);
            reviewRepository.save(review);

        }
    }

    public HashMap<String, String> scrapeReviewPage(Document doc){
        HashMap<String, String> categoryText = new HashMap<>();
        Element article = doc.selectFirst("div#article-body");
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

    public static void main(String[] args){

//        WhatHifi hifi = new WhatHifi();
//        hifi.crawl("https://www.whathifi.com/us/products/headphones/page/6");
    }
}
