package com.example.ratingfinder.Repository;

import com.example.ratingfinder.models.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface UserRepository extends JpaRepository<User,Integer> {

    @Query("select u from User u where u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);

    @Query(value ="SELECT count(user_id) from user where username = :username",nativeQuery = true)
    int countNames(@Param("username") String username);

}
