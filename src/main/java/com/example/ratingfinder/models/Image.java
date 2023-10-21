package com.example.ratingfinder.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data //Creates the getters and setters
public class Image {
    @Id
    @Column(name="image_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int image_id;

    private String file_name;

    @Lob
    @Column(name = "image", columnDefinition = "MEDIUMBLOB")
    private byte[] image;

    //A user-review may have many images
    @ManyToOne
    @JoinColumn(name="user_review_id")
    @OnDelete(action= OnDeleteAction.CASCADE)
    private UserReview user_review_id;
}

