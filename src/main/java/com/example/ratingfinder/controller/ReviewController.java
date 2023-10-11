package com.example.ratingfinder.controller;

import com.example.ratingfinder.models.Review;
import com.example.ratingfinder.models.dto.ReviewDTO;
import com.example.ratingfinder.service.ReviewService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
/**
 * Review Controller responsible for handling requests
 * Should also allow User to make a review for a product
 */
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService){
        this.reviewService = reviewService;
    }

    @GetMapping("/product/{id}/review")
    public List<ReviewDTO> getReviewForProduct(@PathVariable int id){
       List<Review> reviews = reviewService.getReviewForId(id);
       List<ReviewDTO> reviewDTO = new ArrayList<ReviewDTO>();

       for(Review r : reviews){
            ReviewDTO rDTO = new ReviewDTO();
            rDTO.setCategory(r.getCategory());
            rDTO.setCompany(r.getCompany());
            rDTO.setSummary(r.getSummary());
            reviewDTO.add(rDTO);
       }
       return reviewDTO;
    }


}
