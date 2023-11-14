package com.example.ratingfinder.controller;


import com.example.ratingfinder.models.User;
import com.example.ratingfinder.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.ibatis.jdbc.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class MainAPIs {

private final UserService userService;

//users CRUD apis

    @Autowired
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
    public ResponseEntity<String> createUser( User newUser)
    {

        userService.saveUser(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body("success created new user id "+ newUser.getUser_id());
    }

    @GetMapping("/api/getUserById")
    public ResponseEntity<User> getUserById(@RequestParam int id)
    {
        //id = 1;
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

    //signIn api ask front-end pass a map contains username and password
    //return not found if user's password is not correct or username not found
    //return success signed in message and set up the user web session if pwd and usn match


    @PostMapping("/api/signIn")
    public ResponseEntity<String> signIn(HttpServletRequest request,@RequestBody Map<String,String>credentials)
    {

        String username = credentials.get("username");
        String password = credentials.get("password");
        User currentUser = authentication(username,password);
        if(currentUser!=null)
        {
            HttpSession session = request.getSession();
            session.setAttribute("currentUser",currentUser);
            return ResponseEntity.status(HttpStatus.OK).body("success sign in " + currentUser.getUsername());
        }
        else {
            return  ResponseEntity.status(HttpStatus.OK).body("username or password incorrect " + username);
        }

    }

    //validate is a username exist and if it matches this password
    public User authentication (String username,String password)
    {

        User attemptUser;
        //userService.deleteUser(3);
        User attempt  = (User)userService.getUserByUsername(username);

        if(attempt!=null)
        {

            if(password.equals(attempt.getPassword()))
            {
                return attempt;
            }
        }
        return null;
    }

    @PostMapping("/api/SignUp")
    public ResponseEntity<String> signUp(@RequestBody User newUser)
    {
        if(userService.countName(newUser.getUsername()) == 0)
        {
            User verify = userService.saveUser(newUser);
            if(verify!=null)
            {
                return ResponseEntity.status(HttpStatus.OK).body("New user has been signed Up " + newUser.getUsername());
            }
            return ResponseEntity.status(HttpStatus.OK).body("failed save" + newUser.getUsername());
        }
        else {
            return ResponseEntity.status(HttpStatus.OK).body("this name has been occupied " + newUser.getUsername());
        }

    }

    @GetMapping("/api/test")
    public String tester ()
    {
        User testUser = new User();
        testUser.setUsername("newUser");
        testUser.setPassword("abcdef");
        testUser.setEmail("test@email.com");
        testUser.setCredit_level(2);
        if(userService.countName(testUser.getUsername()) == 0)
        {
            User verify = userService.saveUser(testUser);
            if(verify!=null)
            {
                return "New user has been signed Up";
            }
            return "faild";
        }
        return "duplicated name";
    }



}
