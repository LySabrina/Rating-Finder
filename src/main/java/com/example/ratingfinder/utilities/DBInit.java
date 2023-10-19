package com.example.ratingfinder.utilities;

import com.example.ratingfinder.Repository.ProductRepository;
import com.example.ratingfinder.Repository.ReviewRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name="app.db-init", havingValue ="true")
public class DBInit implements CommandLineRunner {


    private final ProductRepository productRepository;


    private final ReviewRepository reviewRepository;
    private final ChatGPT CHAT_GPT;

    private final TechRadarScrape techRadarScrape;
    private final WhatHifi hifi;
    private final TechRadar techRadar;
    public DBInit(ProductRepository productRepository, ReviewRepository reviewRepository, ChatGPT CHAT_GPT, TechRadarScrape techRadarScrape, WhatHifi hifi, TechRadar techRadar){
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
        this.CHAT_GPT = CHAT_GPT;
        this.techRadarScrape = techRadarScrape;
        this.hifi = hifi;
        this.techRadar = techRadar;
    }
    @Override
    public void run(String... args) throws Exception {
        //Optional to clear out your repository
//        reviewRepository.deleteAll();
//        productRepository.deleteAll();


        // <!----------- PHONE ----------->
        hifi.crawl("https://www.whathifi.com/us/products/tablets-and-smartphones", "Phone");
        //UNCOMMENT BELOW TO SCRPAE FROM ANOTHER SITE
//        techRadar.crawl("https://www.techradar.com/phones/reviews/", "Phone");
        System.out.println("---- FINISHED SCRAPING + SUMMARIZING");


    }

}
