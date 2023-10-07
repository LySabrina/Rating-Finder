package com.example.ratingfinder.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data //Generates a toString(), getters, and setters
public class Audio{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String productName;

    private double price;

    private String brand;   //Apple, Beats, Sony

    private String type;    //Example: Headphones, Earbuds

}