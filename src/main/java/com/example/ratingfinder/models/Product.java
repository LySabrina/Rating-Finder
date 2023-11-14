package com.example.ratingfinder.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Data //Generates a toString(), getters, and setters
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Lob
    @Column(name="image", columnDefinition = "MEDIUMBLOB")
    private byte[] image;

    //Default value is 0
    @Column(name="rating")
    private int rating = 0;

    public Product(int prod_id, String name, String brand, double price, String type, byte[] image, int rating) {
        Prod_id = prod_id;
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.type = type;
        this.image = image;
        this.rating = rating;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Prod_id == product.Prod_id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Prod_id);
    }


    public Product() {

    }
}

