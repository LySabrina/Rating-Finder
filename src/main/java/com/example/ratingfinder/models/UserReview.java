package com.example.ratingfinder.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
@Data //automatically creates the getter and setters
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


////    //MappedBy is the variable inside Image
//    @OneToMany(mappedBy = "user_review_id" )
//   private List<Image> images;



}
