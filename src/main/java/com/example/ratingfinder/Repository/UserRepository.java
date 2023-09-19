package com.example.ratingfinder.Repository;

import com.example.ratingfinder.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {
}
