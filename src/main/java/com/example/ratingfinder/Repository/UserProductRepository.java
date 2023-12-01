package com.example.ratingfinder.Repository;

import com.example.ratingfinder.models.UserProduct;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserProductRepository extends JpaRepository<UserProduct, Integer> {

    @Query(value="SELECT product_id from user_product where user_id =:user_id", nativeQuery = true)
    public List<Integer> getProductId(int user_id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM user_product where user_id =:user_id AND product_id =:product_id", nativeQuery = true)
    public int deleteSavedProduct (int product_id, int user_id);
}
