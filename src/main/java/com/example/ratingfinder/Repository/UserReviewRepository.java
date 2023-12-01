package com.example.ratingfinder.Repository;


import com.example.ratingfinder.models.UserReview;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserReviewRepository extends JpaRepository<UserReview, Integer> {

    @Query(value = "Select * FROM user_review WHERE product_id =:id", nativeQuery = true)
    public List<UserReview> getUserReviewForProduct(@Param("id") int id);

    @Query(value = "SELECT avg(rating) from user_review where product_id =:id", nativeQuery = true)
    public int getAvgRatingForProduct(@Param("id") int id);

    @Query(value = "SELECT * from user_review where user_id =:user_id", nativeQuery = true)
    public List<UserReview> findAllUserReviewByUserId(@Param("user_id") int user_id);

    @Query(value = "SELECT * FROM user_review where user_id =:userId AND product_id =:productId", nativeQuery = true)
    public UserReview getUserReview(int userId, int productId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE user_review set rating =:rating, review_text =:review_text where user_id = :user_id AND product_id = :product_id", nativeQuery = true)
    public int update(@Param("user_id") int user_id, @Param("rating") int rating, @Param("review_text") String review_text, @Param("product_id") int product_id);


    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_review where user_id = :user_id AND product_id = :product_id", nativeQuery = true)
    public int delete(@Param("user_id") int user_id, @Param("product_id") int product_id);


}
