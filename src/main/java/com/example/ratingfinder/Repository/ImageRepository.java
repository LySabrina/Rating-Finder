package com.example.ratingfinder.Repository;

import com.example.ratingfinder.models.Image;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Integer> {

    @Query(value = "SELECT image from image where user_review_id =:user_review_id", nativeQuery = true)
    public List<byte[]> getUserReviewImages(@Param("user_review_id") int user_review_id);
}
