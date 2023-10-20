package com.example.ratingfinder;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.example.ratingfinder.controller","com.example.ratingfinder.models","com.example.ratingfinder.Repository","com.example.ratingfinder.service", "com.example.ratingfinder.utilities"})

public class RatingFinderApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(RatingFinderApplication.class, args);

    }
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(this.getClass());
    }

}
