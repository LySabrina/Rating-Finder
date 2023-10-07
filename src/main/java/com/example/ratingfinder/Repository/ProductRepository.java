package com.example.ratingfinder.Repository;

import com.example.ratingfinder.models.Product;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query(value = "SELECT prod_id from Product where product_name = :prod_name", nativeQuery = true)
    int getIdFromName(@Param("prod_name") String prod_name);

    @Query(value = "SELECT DISTINCT product_brand from Product", nativeQuery = true)
    List<String> getBrands();



}
