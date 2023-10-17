package com.example.ratingfinder.Repository;


import com.example.ratingfinder.models.UserReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserReviewRepository extends JpaRepository<UserReview, Integer> {

    @Query(value = "Select * FROM user_review WHERE product_id =:id", nativeQuery = true)
    public List<UserReview> getUserReviewForProduct(@Param("id")int id);

}
