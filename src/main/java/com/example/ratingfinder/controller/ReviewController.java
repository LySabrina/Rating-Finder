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
@CrossOrigin(origins = "http://localhost:3000")
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
    //Get all Professional Reviews for a product
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

    //Get UserReview made by User ID
    @GetMapping("/user/{id}/userReview")
    public ResponseEntity<List<UserReviewDTO>> getUserReviewForId(@PathVariable int id){
        List<UserReviewDTO> userReviewDTOS = userReviewService.getUserReviewsForId(id);
        return ResponseEntity.ok(userReviewDTOS);
    }

    //Returns image as a byte[]
//    @GetMapping("/product/getPicture")
//    public ResponseEntity<List<byte[]>> getPicture(){
//
//        List<byte[]> n = imageService.getUserReviewImages(3);
//        return ResponseEntity.ok(n);
//    }

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

            userReview.setDate(userReviewDTO.getDate());


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
            else{
                System.out.println("FILE IS FALSE" );
            }

            int newAvgRating = userReviewService.getAvgRatingForProduct(p.getProd_id());
            productService.setRating(newAvgRating, p.getProd_id());



            return new ResponseEntity<>("Successfully created review", HttpStatus.OK);
        }
        catch (Exception e){
            e.printStackTrace();

        }
        return new ResponseEntity<String>("Failed to create review", HttpStatus.BAD_REQUEST);
    }


    //Update Review
//    @PostMapping("/update")
//    public ResponseEntity<String> updateUserReview(@RequestBody UserReviewDTO userReviewDTO){
//        int user_id = userReviewDTO.getUser_id();
//        int product_id = userReviewDTO.getProduct_id();
//        System.out.println("USER ID " + user_id);
//        System.out.println( "prod uid" + product_id);
//        boolean update = userReviewService.update(userReviewDTO);
//
//        if(update){
//            return new ResponseEntity<String>("Successfully updated product_id : " + product_id + " for user_id : " + user_id, HttpStatus.OK );
//
//        }
//        else{
//            return new ResponseEntity<String>("FAILED TO updated product_id : " + product_id + " for user_id : " + user_id, HttpStatus.CONFLICT);
//        }
//    }

    @PostMapping("/update")
    public ResponseEntity<String> updateUserReview(@RequestPart("data") UserReviewDTO userReviewDTO, @RequestPart(value="file", required = false) List<MultipartFile> file){
        int user_id = userReviewDTO.getUser_id();
        int product_id = userReviewDTO.getProduct_id();
        System.out.println("USER ID " + user_id);
        System.out.println( "prod uid" + product_id);
        boolean update = userReviewService.update(userReviewDTO);
        UserReview ur = userReviewService.getUserReview(user_id, product_id);
        int newRating = userReviewService.getAvgRatingForProduct(product_id);
        productService.setRating(newRating, product_id);

        if(file != null){
            updateUserReviewImages(file, ur);
        }
        if(update){
            System.out.println("Successfully updated product_id : " + product_id + " for user_id : " + user_id);
            return new ResponseEntity<String>("Successfully updated product_id : " + product_id + " for user_id : " + user_id, HttpStatus.OK );

        }
        else{
            return new ResponseEntity<String>("FAILED TO updated product_id : " + product_id + " for user_id : " + user_id, HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/update/images")
    public void updateUserReviewImages(@RequestPart(value = "file")List<MultipartFile> file, UserReview ur){
        try{

                for(MultipartFile f : file){
                    String fileName = StringUtils.cleanPath(f.getOriginalFilename());
                    byte[] imgBytes = f.getBytes();
                    System.out.println("FILENAME - " + fileName);
                    Image img = new Image();
                    img.setFile_name(fileName);
                    img.setImage(imgBytes);
                    img.setUser_review_id(ur);
                    imageService.saveImage(img);
                }

        }
        catch (Exception e){
            e.printStackTrace();
        }


    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUserReview (@RequestBody UserReviewDTO userReviewDTO){
        int user_id = userReviewDTO.getUser_id();
        int product_id = userReviewDTO.getProduct_id();
        boolean delete = userReviewService.delete(userReviewDTO);

        if(delete){
            return new ResponseEntity<String>("Successfully deleted review with product_id : " + product_id + " for user_id : " + user_id, HttpStatus.OK );

        }
        else{
            return new ResponseEntity<String>("FAILED TO delete review with product_id : " + product_id + " for user_id : " + user_id, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/image")
    public ResponseEntity<String> deleteImages (@RequestParam int id){
        String response = imageService.deleteImage(id);
        return new ResponseEntity<String>(response, HttpStatus.OK);
    }

}
