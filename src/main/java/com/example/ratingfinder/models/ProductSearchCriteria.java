package com.example.ratingfinder.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProductSearchCriteria {
    private List<String> brands ;
    private List<String> product_type ;
//    private String brands;
//    private String product_type;
    private double minPrice = 0;
    private double maxPrice = 20000;
}
