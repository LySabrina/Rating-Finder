package com.example.ratingfinder.models.dto;

import lombok.Data;

@Data
public class ImageDTO {
    private int imageId;
    private String file_name;
    private byte[] photos;
}
