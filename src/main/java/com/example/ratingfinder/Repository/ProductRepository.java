package com.example.ratingfinder.Repository;

import com.example.ratingfinder.models.Product;
import jakarta.transaction.Transactional;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query(value = "SELECT prod_id from Product where product_name = :prod_name", nativeQuery = true)
    int getIdFromName(@Param("prod_name") String prod_name);

    @Query(value = "SELECT DISTINCT product_brand from Product", nativeQuery = true)
    List<String> getBrands();


    @Query(value ="SELECT DISTINCT product_type from Product", nativeQuery = true)
    List<String> getProductTypes();

    @Modifying  //to allow us to perform UPDATE operations
    @Transactional  //add this to declare as a transaction. W/o this, Springboot will be upset and say we are modifying DB without transaction
    @Query(value = "UPDATE product SET rating =:rating WHERE prod_id =:id", nativeQuery = true)
    void setProductRating(@Param("rating")int rating, @Param("id") int id);


    @Query(value = "SELECT DISTINCT price from product", nativeQuery = true)
    List<Double> getAllPrices();
}
