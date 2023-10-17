package com.example.ratingfinder.utilities;

import com.example.ratingfinder.models.Product;
import com.example.ratingfinder.models.Review;
import com.example.ratingfinder.Repository.ProductRepository;
import com.example.ratingfinder.Repository.ReviewRepository;
import com.example.ratingfinder.webscraping.ConvertToDB;
import com.example.ratingfinder.webscraping.SonyHeadphones;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Component
@ConditionalOnProperty(name="app.db-init", havingValue ="true")
public class DBInit implements CommandLineRunner {


    private final ProductRepository productRepository;


    private final ReviewRepository reviewRepository;
    private final ChatGPT CHAT_GPT;

    private final TechRadar techRadar;
    public DBInit(ProductRepository productRepository, ReviewRepository reviewRepository, ChatGPT CHAT_GPT, TechRadar techRadar){
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
        this.CHAT_GPT = CHAT_GPT;
        this.techRadar = techRadar;
    }
    @Override
    public void run(String... args) throws Exception {
//        reviewRepository.deleteAll();
//        productRepository.deleteAll();

        //Saving Sony Headphone products to the database
//        ArrayList<Product> prods = SonyHeadphones.getSonyProducts();
//        productRepository.saveAll(prods);
//        System.out.println("SAVED PRODUCTS");
////
////        //Fetching all the Products recently added to the database
//        List<Product> products = productRepository.findAll();
////
////        //Summarzing each Sony product from TrustedReview website
//        for(Product product : products){
//
//            //Contains the pair <CATEGORY, TEXT>
//            HashMap<String, String> categoryText = TrustedReviewScrape.getReviews(product.getName());
//
//            //If the article can not be for TrustedReviews, skip
//            if(categoryText == null){
//                continue;
//            }
//
//            Set<String> keys = categoryText.keySet();
//            //Creating Review objects that has summarized text by TrustedReview
//            for(String category : keys){
//                String text = categoryText.get(category);
//
//                String summarizedText = CHAT_GPT.chatGPT(  "Summarize this in a few sentences: " + text);
////                System.out.println("SUMMARIZED TEXT = " + summarizedText);
//                Review review = new Review();
//                review.setCompany("Trusted Review");
//                review.setCategory(category);
//                review.setProduct(product);
//                review.setSummary(summarizedText);
//                reviewRepository.save(review);
//
//                System.out.println("REVIEW: " + review.toString() + " FOR PRODUCT - " + product.getName() + " w/ID: " + product.getProd_id());
//            }
//        }
//
//        //Summarizing each product from HiFi Scrape website
//        for(Product product: products){
//            HashMap<String, String> categoryText = HiFiScrape.getReviews(product.getName());
//            if(categoryText == null){
//                continue;
//            }
//
//            Set<String> keys = categoryText.keySet();
//            //Creating Review objects that has summarized text by WhatHifi
//            for(String category : keys){
//                String text = categoryText.get(category);
//                String summarizedText = CHAT_GPT.chatGPT ("Summarize this in a few sentences: " + text);
////                System.out.println("SUMMARIZED TEXT = " + summarizedText);
//                Review review = new Review();
//                review.setCompany("What HiFi");
//                review.setCategory(category);
//                review.setProduct(product);
//                review.setSummary(summarizedText);
//                reviewRepository.save(review);
//
//                System.out.println("REVIEW: " + review.toString() + " FOR PRODUCT - " + product.getName() + " w/ID: " + product.getProd_id());
//            }
//        }

//        List<Product> products = ConvertToDB.readJSONToDB();
//        System.out.println(products.toString());
//        productRepository.saveAll(products);


        techRadar.crawl();
        System.out.println("---- FINISHED SCRAPING + SUMMARIZING");


    }

}
