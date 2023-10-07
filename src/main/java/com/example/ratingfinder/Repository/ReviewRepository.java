package com.example.ratingfinder.Repository;



import com.example.ratingfinder.models.Review;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    @Query(value = "Select * from review where product_id =:product_id", nativeQuery = true)
    List<Review> getReviewsForId(@Param("product_id")int product_id);
}
