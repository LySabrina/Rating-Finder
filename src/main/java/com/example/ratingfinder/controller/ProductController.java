package com.example.ratingfinder.controller;

import com.example.ratingfinder.models.Product;
import com.example.ratingfinder.models.ProductPage;

import com.example.ratingfinder.models.ProductSearchCriteria;
import com.example.ratingfinder.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller to handle API points related to Products
 */
@RestController     //Automatically converts results to JSON format
@CrossOrigin(origins = "http://localhost:3000"
)
public class ProductController {

    private final ProductService productService;
    public ProductController(ProductService service){
        this.productService = service;
    }

    @GetMapping("/product/allProduct")
    public List<Product> getAllProducts(){
        return productService.getAllProducts();
    }

    @GetMapping("/product")
    public Page<Product> getProductsByPage(ProductPage productPage, ProductSearchCriteria productSearchCriteria){
//        Pageable pageable = PageRequest.of(productPage.getPageNumber(), productPage.getPageSize());
        return productService.filter(productPage, productSearchCriteria);
//        return productService.getProductByPages(pageable);
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


    @PostMapping("/save/{id}")
    public ResponseEntity<String> saveProduct(@PathVariable int id, @RequestParam("userId") int user_id){

        String response = productService.saveProduct(id, user_id);
        return new ResponseEntity<String>(response, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteSavedProduct (@PathVariable int id, @RequestParam("userId") int user_id){
        String response = productService.deleteSavedProduct(id, user_id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/user/save/{id}")
    public ResponseEntity<List<Product>> getSavedProducts(@PathVariable int id){
        List<Product> savedProducts = productService.getSavedProducts(id);
        return new ResponseEntity<List<Product>>(savedProducts, HttpStatus.OK);
    }


    //Implement some filtering of products
//    @GetMapping("/product/filter")
//    public List<Product> getFilteredProducts(@RequestParam(required = false) List<String> brands, @RequestParam(required = false) String product_type, @RequestParam double minPrice, @RequestParam double maxPrice){
//        return productService.getFilterProducts(brands, product_type, minPrice, maxPrice);
//
//    }


//    @GetMapping("/product/test")
//    public List<Product> filter(@RequestParam(required = false) List<String> brands, @RequestParam(required = false) String product_type, @RequestParam double minPrice, @RequestParam double maxPrice){
//        return
//    }
//    @GetMapping("/product/existing/")
//    public Integer getExistingProductID(){
//        return productService.getExistingProductID("iPhone 15 Pro Max");
//    }
//
//    @GetMapping("/product/nonexist/")
//    public Integer getNonExist(){
//        return productService.getExistingProductID("Sony Xperia 5 V");
//    }


}
