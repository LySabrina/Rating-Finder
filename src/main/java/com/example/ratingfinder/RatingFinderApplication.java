package com.example.ratingfinder;


//import com.example.ratingfinder.config.YoutubeConfigProperties;
//import com.example.ratingfinder.service.TrustedReviewScrape;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class RatingFinderApplication {

    public static void main(String[] args) {
        SpringApplication.run(RatingFinderApplication.class, args);

    }

}
