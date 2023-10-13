package com.example.ratingfinder.models;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
public class UserReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userReviewId;

    @ManyToOne
    @JoinColumn(name="user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user_id;

    @Column(columnDefinition = "TEXT")
    private String review_text;

   @ManyToOne
   @JoinColumn(name="product_id")
   @OnDelete(action=OnDeleteAction.CASCADE)
   private Product product;

   //Score out of 5 stars
   private int rating;

}
