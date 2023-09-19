package com.example.ratingfinder.controller;


import com.example.ratingfinder.models.User;
import com.example.ratingfinder.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class MainAPIs {

private final UserService userService;

//users CRUD apis

    public MainAPIs(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/getAllUsers")
    public ResponseEntity<List<User>> getAllUsers()
    {
        List<User> list= userService.getAllUsers();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/api/createUser")
    public ResponseEntity<String> createUser(@RequestParam User newUser)
    {

        userService.saveUser(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body("success created new user id "+ newUser.getUser_id());
    }

    @GetMapping("/api/getUserById")
    public ResponseEntity<User> getUserById(@RequestParam int id)
    {
        id = 1;
        Optional<User> ou = userService.getUserById(id);
        return ou.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/updateUser")
    public ResponseEntity<String> updateUser(@RequestParam User targetUser){
        if(!userService.getUserRepository().existsById(targetUser.getUser_id()))
        {
            return ResponseEntity.notFound().build();
        }
        User u =(User)userService.saveUser(targetUser);
        return ResponseEntity.status(HttpStatus.OK).body("update user: "+ targetUser.getUser_id());

    }
    @DeleteMapping("/api/deleteUser")
    public ResponseEntity<String> deleteUser(@RequestParam int id)
    {
        if(!userService.getUserRepository().existsById(id))
        {
            return ResponseEntity.notFound().build();
        }
        userService.deleteUser(id);
        return ResponseEntity.status(HttpStatus.OK).body("deleted user id:" + id);
    }


}
