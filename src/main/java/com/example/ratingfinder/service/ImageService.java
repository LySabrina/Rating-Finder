package com.example.ratingfinder.service;

import com.example.ratingfinder.Repository.ImageRepository;
import com.example.ratingfinder.models.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ImageService {
    private final ImageRepository imageRepository;

    @Autowired
    public ImageService(ImageRepository imageRepository){
        this.imageRepository = imageRepository;
    }

    public Image saveImage(Image image){
        return imageRepository.save(image);
    }

    public List<byte[]> getUserReviewImages(int user_review_id){
        return imageRepository.getUserReviewImages(user_review_id);
    }
}
