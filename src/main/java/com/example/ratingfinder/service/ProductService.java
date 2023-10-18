package com.example.ratingfinder.service;


import com.example.ratingfinder.models.Product;
import com.example.ratingfinder.Repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final UserReviewService userReviewService;
    @Autowired
    public ProductService(ProductRepository productRepository, UserReviewService userReviewService){
        this.productRepository = productRepository;
        this.userReviewService = userReviewService;
    }

    public List<String> getBrands(){
        return productRepository.getBrands();
    }

    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    public List<String> getProductTypes(){return productRepository.getProductTypes();}

    public Product getProductById(int id){
        return productRepository.findById(id).get();
    }

    public void setRating(int ratingVal, int prod_id){
        int avgRating = userReviewService.getAvgRatingForProduct(prod_id);
        productRepository.setProductRating(avgRating, prod_id);
    }

    public List<Double> getAllPrices(){
        return productRepository.getAllPrices();
    }

}
