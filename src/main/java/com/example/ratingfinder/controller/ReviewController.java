package com.example.ratingfinder.controller;

import com.example.ratingfinder.models.Review;
import com.example.ratingfinder.service.ReviewService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService){
        this.reviewService = reviewService;
    }

    @GetMapping("/product/{id}/review")
    public List<Review> getReviewForProduct(@PathVariable int id){
        return reviewService.getReviewForId(id);
    }
}
