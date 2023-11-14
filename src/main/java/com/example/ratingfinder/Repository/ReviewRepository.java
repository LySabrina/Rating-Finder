package com.example.ratingfinder.Repository;
import com.example.ratingfinder.models.Review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    @Query(value = "Select * from review where product_id =:product_id", nativeQuery = true)
    List<Review> getReviewsForId(@Param("product_id")int product_id);


}

