package com.example.ratingfinder.Repository;

import com.example.ratingfinder.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SearchingRepository extends JpaRepository<Product,Integer> {


}
