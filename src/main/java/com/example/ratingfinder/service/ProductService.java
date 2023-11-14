package com.example.ratingfinder.service;


import com.example.ratingfinder.Repository.ProductCriteriaRepository;
import com.example.ratingfinder.models.Product;
import com.example.ratingfinder.Repository.ProductRepository;
import com.example.ratingfinder.models.ProductPage;
import com.example.ratingfinder.models.ProductSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final UserReviewService userReviewService;

    private final ProductCriteriaRepository productCriteriaRepository;
    @Autowired
    public ProductService(ProductRepository productRepository, UserReviewService userReviewService, ProductCriteriaRepository productCriteriaRepository){
        this.productRepository = productRepository;
        this.userReviewService = userReviewService;
        this.productCriteriaRepository = productCriteriaRepository;

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

    public Product getExistingProductID(String name){
        return productRepository.getExistingProductID(name);
    }

    public Product save(Product p){
        return productRepository.save(p);
    }

    public Page<Product> getProductByPages(Pageable pageable){
        return productRepository.findAll(pageable);
    }

    public Page<Product> filter(ProductPage productPage, ProductSearchCriteria productSearchCriteria){
        return productCriteriaRepository.findProductsWithFilter(productPage, productSearchCriteria);
    }

}
