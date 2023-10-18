package com.example.ratingfinder.controller;

import com.example.ratingfinder.models.Product;
import com.example.ratingfinder.service.ProductService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {

    private final ProductService productService;
    public ProductController(ProductService service){
        this.productService = service;
    }

    @GetMapping("/product/allProduct")
    public List<Product> getAllProducts(){
        return productService.getAllProducts();
    }

    @GetMapping("/product/brands")
    public List<String> getBrands(){
        return productService.getBrands();
    }

    @GetMapping("/product/productType")
    public List<String> getProductTypes(){
        return productService.getProductTypes();
    }

    @GetMapping("/product/{id}")
    public Product getProductById(@PathVariable int id){
        return productService.getProductById(id);
    }

    @GetMapping("/product/prices")
    public List<Double> getAllPrices(){
        return productService.getAllPrices();
    }
}
