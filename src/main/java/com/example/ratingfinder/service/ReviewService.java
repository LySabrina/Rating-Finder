package com.example.ratingfinder.service;

import com.example.ratingfinder.models.Review;
import com.example.ratingfinder.Repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository){
        this.reviewRepository = reviewRepository;
    }

    public List<Review> getReviewForId(int id){
       return reviewRepository.getReviewsForId(id);
    }

    public void flush(){
        reviewRepository.flush();
    }

    public Review save(Review r){
        return reviewRepository.save(r);
    }
}
