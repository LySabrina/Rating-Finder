package com.example.ratingfinder.service;


import com.example.ratingfinder.Repository.ProductCriteriaRepository;
import com.example.ratingfinder.Repository.UserProductRepository;
import com.example.ratingfinder.models.*;
import com.example.ratingfinder.Repository.ProductRepository;
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
    private final UserProductRepository userProductRepository;
    private final UserService userService;
    @Autowired
    public ProductService(ProductRepository productRepository, UserReviewService userReviewService, ProductCriteriaRepository productCriteriaRepository, UserProductRepository userProductRepository, UserService userService){
        this.productRepository = productRepository;
        this.userReviewService = userReviewService;
        this.userProductRepository = userProductRepository;
        this.productCriteriaRepository = productCriteriaRepository;
        this.userService = userService;
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

    public String saveProduct(int product_id, int user_id){
        UserProduct userProduct = new UserProduct();
        Product p = productRepository.getReferenceById(product_id);
        User u = userService.getUserById(user_id).get();
        userProduct.setUser(u);
        userProduct.setProduct(p);
        userProductRepository.save(userProduct);
        return "Saved Product id: " + product_id + " for user: " + user_id;
    }

    public List<Product> getSavedProducts (int user_id){
        List<Integer> savedproductsId = userProductRepository.getProductId(user_id);
        return productRepository.findAllById(savedproductsId);
    }

    public String deleteSavedProduct (int product_id, int user_id){
       int deleteRows = userProductRepository.deleteSavedProduct(product_id, user_id);
       if(deleteRows > 0){
           return "Successfully deleted product - " + product_id +" for user_id- " + user_id;
       }
       return "FAILED TO DELETE";

    }
}
