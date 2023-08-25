package com.example.ratingfinder;


import com.example.ratingfinder.config.YoutubeConfigProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class RatingFinderApplication {

    public static void main(String[] args) {
        SpringApplication.run(RatingFinderApplication.class, args);
    }

}
