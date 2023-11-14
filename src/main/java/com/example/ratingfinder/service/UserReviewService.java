package com.example.ratingfinder.service;

import com.example.ratingfinder.Repository.ProductRepository;
import com.example.ratingfinder.Repository.UserReviewRepository;
import com.example.ratingfinder.models.*;
import com.example.ratingfinder.models.dto.UserReviewDTO;

import org.springframework.stereotype.Service;


import java.util.ArrayList;

import java.util.List;

@Service
public class UserReviewService {
    private final UserReviewRepository userReviewRepository;
    private final UserService userService;

    private final ImageService imageService;

    public UserReviewService(UserReviewRepository userReviewRepository, ImageService imageService, UserService userService){
        this.userReviewRepository = userReviewRepository;
        this.imageService = imageService;
        this.userService = userService;

    }



    public UserReview addUserReview(UserReview userReview){
        return userReviewRepository.save(userReview);
    }

    public List<UserReviewDTO> getUserReviewForProduct(int id){
        List<UserReview> userReviews = userReviewRepository.getUserReviewForProduct(id);
        List<UserReviewDTO> dtos = new ArrayList<UserReviewDTO>();

        for(UserReview r :userReviews){
            UserReviewDTO userReviewDTO = new UserReviewDTO();
            userReviewDTO.setUser_id(r.getUser_id().getUser_id());
            userReviewDTO.setReview_text(r.getReview_text());
            userReviewDTO.setRating(r.getRating());
            userReviewDTO.setProduct_id(r.getProduct().getProd_id());
            int user_review_id = r.getUserReviewId();
            String username = r.getUser_id().getUsername();
            userReviewDTO.setUsername(username);
            List<byte[]> photos = imageService.getUserReviewImages(user_review_id);
            userReviewDTO.setPhotos(photos);

            dtos.add(userReviewDTO);
        }
        return dtos;
    }

    public int getAvgRatingForProduct(int id){
        return userReviewRepository.getAvgRatingForProduct(id);
    }
    public void flush(){
        userReviewRepository.flush();
    }

    public List<UserReviewDTO> getUserReviewsForId(int userId){
        List<UserReviewDTO> userReviewDTOList = new ArrayList<>();
        List<UserReview> reviews = userReviewRepository.findAllUserReviewByUserId(userId);
        System.out.println(reviews.size());

        for(UserReview r : reviews){
            UserReviewDTO userReviewDTO = new UserReviewDTO();
            userReviewDTO.setRating(r.getRating());
            userReviewDTO.setReview_text(r.getReview_text());
            userReviewDTO.setDate(r.getDate());
            List<byte[]> photos = imageService.getUserReviewImages(r.getUserReviewId());
            userReviewDTO.setPhotos(photos);
            Product p = r.getProduct();
            String prodName = p.getName();
            userReviewDTO.setProductName(prodName);

            userReviewDTOList.add(userReviewDTO);
        }
        return userReviewDTOList;
    }
}
