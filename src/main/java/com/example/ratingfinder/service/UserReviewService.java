package com.example.ratingfinder.service;

import com.example.ratingfinder.Repository.UserReviewRepository;
import com.example.ratingfinder.models.*;
import com.example.ratingfinder.models.dto.UserReviewDTO;

import org.springframework.stereotype.Service;


import java.util.ArrayList;

import java.util.List;

@Service
public class UserReviewService {
    private final UserReviewRepository userReviewRepository;

    private final ImageService imageService;
    public UserReviewService(UserReviewRepository userReviewRepository, ImageService imageService){
        this.userReviewRepository = userReviewRepository;
        this.imageService = imageService;
    }



    public UserReview addUserReview(UserReview userReview){
        return userReviewRepository.save(userReview);
    }

    public List<UserReviewDTO> getUserReviewForProduct(int id){
        List<UserReview> userReviews = userReviewRepository.getUserReviewForProduct(id);
        System.out.println(userReviews.toString());
        List<UserReviewDTO> dtos = new ArrayList<UserReviewDTO>();

        for(UserReview r :userReviews){
            UserReviewDTO userReviewDTO = new UserReviewDTO();
            userReviewDTO.setUser_id(r.getUser_id().getUser_id());
            userReviewDTO.setReview_text(r.getReview_text());
            userReviewDTO.setRating(r.getRating());
            userReviewDTO.setProduct_id(r.getProduct().getProd_id());

            List<byte[]> photos = imageService.getUserReviewImages(id);
            userReviewDTO.setPhotos(photos);

            dtos.add(userReviewDTO);
        }
        return dtos;
    }

    public void flush(){
        userReviewRepository.flush();
    }


}
