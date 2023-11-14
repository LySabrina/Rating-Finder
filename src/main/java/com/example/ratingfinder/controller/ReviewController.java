package com.example.ratingfinder.controller;

import com.example.ratingfinder.models.*;
import com.example.ratingfinder.models.dto.ReviewDTO;
import com.example.ratingfinder.models.dto.UserReviewDTO;
import com.example.ratingfinder.service.*;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.ArrayList;

import java.util.List;


@RestController
/**
 * Review Controller responsible for handling requests
 * Handles Requests related to Review and UserReview
 */
public class ReviewController {
    private final ReviewService reviewService;

    private final ProductService productService;
    private final UserService userService;

    private final UserReviewService userReviewService;

    private final ImageService imageService;
    public ReviewController(ReviewService reviewService, ProductService productService, UserService userService, UserReviewService userReviewService, ImageService imageService){
        this.reviewService = reviewService;
        this.productService = productService;
        this.userService = userService;
        this.userReviewService = userReviewService;
        this.imageService = imageService;
    }

    //<!--------------------------- GET MAPPING ----------------------->
    //Get all Professional Reviews for a producr
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


    //Get all userReviews for a product
    @GetMapping("/product/{id}/userReview")
    public ResponseEntity<List<UserReviewDTO>> getUserReviewForProduct(@PathVariable int id){
        List<UserReviewDTO> userReviewDTOS = userReviewService.getUserReviewForProduct(id);
        return ResponseEntity.ok(userReviewDTOS);
    }

    //Returns image as Base64
    @GetMapping("/product/getPicture")
    public ResponseEntity<List<byte[]>> getPicture(){

        List<byte[]> n = imageService.getUserReviewImages(3);
        return ResponseEntity.ok(n);
//        List<UserReviewDTO> r= userReviewService.getUserReviewForProduct(1);
//        UserReviewDTO d = r.get(2);
//        byte[] img = d.getPhoto();
//
//        System.out.println("image created");
//        return ResponseEntity.ok(img);
    }

    //<!--------------------------- POST MAPPING ----------------------->
    //Saves a userReview for a product
    @Cacheable("productCache")
    @PostMapping("/product/{id}/create")
    public ResponseEntity<String> postReview(@RequestPart("data") UserReviewDTO userReviewDTO, @RequestPart(value="file", required = false) List<MultipartFile> file, @PathVariable int id){
        try{
            UserReview userReview = new UserReview();

            //Setting the User_ID, Product, UserRating Number
            User u = userService.getUserById(userReviewDTO.getUser_id()).get();


            userReview.setUser_id(u);
            userReview.setReview_text(userReviewDTO.getReview_text());

            Product p = productService.getProductById(userReviewDTO.getProduct_id());

            userReview.setProduct(p);

            userReview.setRating(userReviewDTO.getRating());



            UserReview addedReview = userReviewService.addUserReview(userReview);
//            userReviewService.flush();

            //Dealing with uploaded photos
            if(file != null){
                for(MultipartFile f : file){
                    String fileName = StringUtils.cleanPath(f.getOriginalFilename());
                    byte[] imgBytes = f.getBytes();
                    System.out.println("FILENAME - " + fileName);
                    Image img = new Image();
                    img.setFile_name(fileName);
                    img.setImage(imgBytes);
                    img.setUser_review_id(addedReview);
                    imageService.saveImage(img);
                }
            }

            int newAvgRating = userReviewService.getAvgRatingForProduct(p.getProd_id());
            productService.setRating(newAvgRating, p.getProd_id());
            //Now that user_review is in the database, we want to update the product's rating


            return new ResponseEntity<>("Successfully created review", HttpStatus.OK);
        }
        catch (Exception e){
            e.printStackTrace();

        }
        return new ResponseEntity<String>("Failed to create review", HttpStatus.BAD_REQUEST);
    }




}
