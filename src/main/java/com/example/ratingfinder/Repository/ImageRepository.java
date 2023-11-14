package com.example.ratingfinder.Repository;

import com.example.ratingfinder.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {

    @Query(value = "SELECT image from image where user_review_id =:user_review_id", nativeQuery = true)
    public List<byte[]> getUserReviewImages(@Param("user_review_id") int user_review_id);
}
