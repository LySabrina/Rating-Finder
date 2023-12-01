package com.example.ratingfinder.models.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class SavedProduct {
    @Id
    private int pair_id;

    private int user_id;
    private int product_id;
}
