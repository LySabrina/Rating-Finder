package com.example.ratingfinder.models;

import lombok.Data;

/**
 * Plain Old Java Object
 * Used to send the data from front-end to back-end
 *
 * Only send the category, company, and summary
 * No need to send toe the front-end the id or prod-id
 */
@Data //makes the constructor, getters, and setters
public class ReviewDTO {
    private String category;
    private String company;

}
