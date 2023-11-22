package com.example.ratingfinder.controller;


import com.example.ratingfinder.models.Response;
import com.example.ratingfinder.models.User;
import com.example.ratingfinder.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class MainAPIs {

private final UserService userService;

private final ReviewService reviewService;

private final ProductService productService;


private final UserReviewService userReviewService;

private final ImageService imageService;

//users CRUD apis
@Autowired
public JdbcTemplate jdbcTemplate;
    @Autowired
    public MainAPIs(ReviewService reviewService, ProductService productService, UserService userService, UserReviewService userReviewService, ImageService imageService)
    {
        this.userService = userService;
        this.reviewService =reviewService;
        this.productService =productService;
        this.userReviewService = userReviewService;
        this.imageService= imageService;

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
    public ResponseEntity<Response> signIn(HttpServletRequest request,@RequestBody Map<String,String>credentials)
    {

        String username = credentials.get("username");
        String password = credentials.get("password");
        User currentUser = authentication(username,password);
        if(currentUser!=null)
        {
            HttpSession session = request.getSession();
            session.setAttribute("currentUser",currentUser);

            Response response = new Response();
            response.setUser(currentUser);
            response.setMessage("Success sign in " + currentUser.getUsername());
            return ResponseEntity.status(HttpStatus.OK).body(response);
//            return ResponseEntity.status(HttpStatus.OK).body("Success sign in " + currentUser.getUsername());

        }
        else {
            Response response = new Response();
            response.setUser(null);
            response.setMessage("username or pwd error");
            return ResponseEntity.status(HttpStatus.OK).body(response);
//            return ResponseEntity.status(HttpStatus.OK).body("username or pwd error");
        }

    }

    //validate is a username exist and if it matches this password
    public User authentication (String username,String password)
    {

        try {
            String sql = "SELECT * FROM user WHERE username = ?";
            List<Map<String,Object>>  list= jdbcTemplate.queryForList(sql,username);
            if(list.size()==1)
            {

                Map<String, Object> map = list.get(0);
                if(map.get("password") .equals(password))
                {
                    Integer userId = (Integer) map.get("user_id");
                    String un = (String) map.get("username");
                    String pw = (String) map.get("password");
                    String email = (String) map.get("email");
                    int creditLevel = (int) map.get("credit_level");

                    User attempt = new User(userId, un, pw, email, creditLevel);
                    return attempt;
                }
                return null;

            }

        } catch (EmptyResultDataAccessException e) {
            // Handle the case where no user is found
            return null;
        }


        return null;
    }

    @PostMapping("/api/signUp")
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

    //getCurrUser api return the current session user if user has been signed in
    //else return notfound.
    @GetMapping("/api/getCurrUser")
    public ResponseEntity<User> getCurrentUser(HttpServletRequest request)
    {
        User curr = (User) request.getSession().getAttribute("currentUser");
        if(curr==null)
        {
            return ResponseEntity.notFound().build();
        }
        else {
            return ResponseEntity.ok(curr);
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
