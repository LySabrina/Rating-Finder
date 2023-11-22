package com.example.ratingfinder.controller;

import com.example.ratingfinder.models.Review;
import com.example.ratingfinder.models.User;
import com.example.ratingfinder.service.ProductService;
import com.example.ratingfinder.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ReviewLikesController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private final ReviewService reviewService;
    private final ProductService productService;

    @Autowired
    public ReviewLikesController(ReviewService reviewService, ProductService productService) {
        this.reviewService =reviewService;
        this.productService = productService;
    }



    //this method record a user like a review, requestBody ask for a pair that contain first param as the
    //review id.
    //return 1 represent 1 row inserted that is success operation
    //return -1 means current user has already liked this review
    @PostMapping("/api/recordReviewLike")
    public int recordReviewLike (HttpServletRequest request,@RequestParam int review_id)
    {
        //check if this user has already liked this review
        List<Integer> alreadyLiked= getUserLikedReview(request).getBody();
        if(alreadyLiked.contains(review_id))
        {
            return -1;
        }

        User current = (User)request.getSession().getAttribute("currentUser");
        String sql = "insert into review_like (`review_id`, `like_giver`) VALUES (?,?)";
        int result = jdbcTemplate.update(sql,review_id,current.getUser_id());
        return result;
    }

    //this api query all the liked given by current session user
    //user are gotten from session and method return a list of review_id which
    //represent the reviews that have already liked by the current user
    @GetMapping("/api/getLikedReview")
    public ResponseEntity<List<Integer>> getUserLikedReview(HttpServletRequest request)
    {
        User current = (User) request.getSession().getAttribute("currentUser");
        String sql ="Select review_id from review_like where like_giver = ?";
        List<Map<String,Object>> l = jdbcTemplate.queryForList(sql,current.getUser_id());
        List<Integer> result = new ArrayList<>();
        for(Map m: l){
            result.add((Integer) m.get("review_id"));
        }
        return ResponseEntity.ok(result);

    }

    //normally system load a list of review to show the review list of certain product,
    //so this method take a product_id as a parameter and get all the numbers of like of
    //all the product review. Return with a map that contains a set of review_id as keys and
    //a set of number of likes as value
    @GetMapping("/api/getReviewWithLikes")
    public ResponseEntity<Map<Integer, Long>> getReviewWithLikes(@RequestParam int prod_id){
        List<Review>  listReview= reviewService.getReviewForId(prod_id);

        Map<Integer,Long> result = new HashMap<>();
        String sql ="SELECT review.review_id, COALESCE(COUNT(review_like.review_id), 0) AS numLike\n" +
                "FROM review\n" +
                "LEFT JOIN review_like ON review.review_id = review_like.review_id\n" +
                "WHERE review.product_id = ? \n" +
                "GROUP BY review.review_id;";
        List<Map<String,Object>> temp = jdbcTemplate.queryForList(sql,prod_id);
        for(Map m: temp)
        {
            result.put((Integer) m.get("review_id"),((Number) m.get("numLike")).longValue());
        }
        if(result.size() == listReview.size())
        {
            return ResponseEntity.ok(result);
        }else {
            return ResponseEntity.notFound().build();
        }

    }


}
