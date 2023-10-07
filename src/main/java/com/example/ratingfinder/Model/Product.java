package com.example.ratingfinder.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data //Generates a toString(), getters, and setters
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "Prod_id")
    private int Prod_id;

    @Column(name = "Product_Name")

    private String name;

    @Column(name="Product_Brand")
    private String brand;

    @Column(name="price")
    private double price;

    @Column(name="Product_Type", nullable = true)
    private String type;    //type like earbuds, headphones, or NULL if not applicable




}
