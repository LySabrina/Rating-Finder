package com.example.ratingfinder.models.dto;

import com.example.ratingfinder.models.Image;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Data
public class UserReviewDTO {

    private int user_id;
    private String username;
    private String review_text;
    private int rating;
    private int product_id;
    private LocalDate date = LocalDate.now();
    private List<byte[]> photos;
    private String productName;
}
