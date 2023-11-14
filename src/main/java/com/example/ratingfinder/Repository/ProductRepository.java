package com.example.ratingfinder.Repository;

import com.example.ratingfinder.models.Product;
import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
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

    //Change to a list maybe?
    @Query(value = "SELECT * from product where product_name LIKE %:name", nativeQuery = true)
    Product getExistingProductID(@Param("name")String name);

// Query causes issues
//    @Query(value = "SELECT * FROM product where product_type =:product_type AND product_brand IN :brands AND price >= :minPrice AND price <= :maxPrice", nativeQuery = true)
//    List<Product> getFilteredProducts(@Param("brands")List<String> brands, @Param("product_type")String product_type, @Param("minPrice") double minPrice, @Param("maxPrice") double maxPrice);


//    @Query(value = "SELECT * from product where product_type LIKE %:type", nativeQuery = true)
//    List<Product> getProductsByType(@Param("type") String type);
//
//    @Query(value = "SELECT * from product where product_name LIKE %:name%", nativeQuery = true)
//    List<Product> getProductsByName(@Param("name") String name);
//
//    @Query(value = "SELECT * from product where product_brand LIKE %:product_brand%", nativeQuery = true)
//    List<Product> getProductByBrand(@Param("name") String product_brand);







}

    @Query(
            value ="Select p from Product as p where" +
                    " (:brands is null or p.brand IN :brands) " +
                    " AND (:product_type IS NULL or p.type = :product_type) " +
                    " AND (:minPrice IS NULL or p.price >= :minprice) " +
                    "AND (:maxPrice IS NULL or p.price <= :maxPrice)")
    List<Product> getFilteredProducts(@Param("brands")List<String> brands, @Param("product_type")String product_type, @Param("minPrice") double minPrice, @Param("maxPrice") double maxPrice);

}

