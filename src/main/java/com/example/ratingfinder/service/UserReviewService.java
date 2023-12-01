package com.example.ratingfinder.service;

import com.example.ratingfinder.Repository.ProductRepository;
import com.example.ratingfinder.Repository.UserReviewRepository;
import com.example.ratingfinder.models.*;
import com.example.ratingfinder.models.dto.ImageDTO;
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


    public UserReview getUserReview(int userId, int productId){
        return userReviewRepository.getUserReview(userId, productId);
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

            List<Image> photos = imageService.getUserReviewImages(user_review_id);
            List<ImageDTO> imageDTOS = new ArrayList<>();
            for(Image i : photos){
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setFile_name(i.getFile_name());
                imageDTO.setPhotos(i.getImage());
                imageDTO.setImageId(i.getImage_id());
                imageDTOS.add(imageDTO);
            }
//            List<byte[]> photos = imageService.getUserReviewImages(user_review_id);

//            userReviewDTO.setPhotos(photos);
            userReviewDTO.setPhotos(imageDTOS);
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


        for(UserReview r : reviews){
            UserReviewDTO userReviewDTO = new UserReviewDTO();
            User username = userService.getUserById(userId).get();
            userReviewDTO.setUsername(username.getUsername());
            userReviewDTO.setRating(r.getRating());
            userReviewDTO.setReview_text(r.getReview_text());
            userReviewDTO.setDate(r.getDate());
            userReviewDTO.setUser_id(userId);
            userReviewDTO.setProduct_id(r.getProduct().getProd_id());

            int userReviewId = r.getUserReviewId();

            List<Image> photos = imageService.getUserReviewImages(userReviewId);
            List<ImageDTO> imageDTOS = new ArrayList<>();
            for(Image i : photos){
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setFile_name(i.getFile_name());
                imageDTO.setPhotos(i.getImage());
                imageDTO.setImageId(i.getImage_id());
                imageDTOS.add(imageDTO);
            }
//            List<byte[]> photos = imageService.getUserReviewImages(r.getUserReviewId());
//            userReviewDTO.setPhotos(photos);
            userReviewDTO.setPhotos(imageDTOS);

            Product p = r.getProduct();
            String prodName = p.getName();
            userReviewDTO.setProductName(prodName);

            userReviewDTOList.add(userReviewDTO);
        }
        return userReviewDTOList;
    }

    public boolean update (UserReviewDTO userReviewDTO){
        int user_id = userReviewDTO.getUser_id();
        int rating = userReviewDTO.getRating();
        String review_text = userReviewDTO.getReview_text();
        int product_id = userReviewDTO.getProduct_id();
        int numUpdated = userReviewRepository.update(user_id, rating, review_text, product_id);
        if(numUpdated > 0){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean delete (UserReviewDTO userReviewDTO){
        int user_id = userReviewDTO.getUser_id();
        int product_id = userReviewDTO.getProduct_id();
        int deleteNum = userReviewRepository.delete(user_id, product_id);
        if(deleteNum > 0){
            return true;
        }
        else{
            return false;
        }
    }
}
