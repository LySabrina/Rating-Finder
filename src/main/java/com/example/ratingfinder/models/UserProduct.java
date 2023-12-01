package com.example.ratingfinder.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "user_product")
public class UserProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
