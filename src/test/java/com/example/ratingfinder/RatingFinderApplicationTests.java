package com.example.ratingfinder;

import com.example.ratingfinder.Repository.UserRepository;
import com.example.ratingfinder.controller.MainAPIs;
import com.example.ratingfinder.models.User;
import com.example.ratingfinder.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
class RatingFinderApplicationTests {
    //private final UserService userService;
//    @InjectMocks
//    private MainAPIs mainAPIs;
//
//    @Mock
//    private  UserRepository userRepository;
//    @Mock
//    private HttpServletRequest request;
//    @Mock
//    private HttpSession session;
//    @InjectMocks
//    private UserService userService;
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        userService = new UserService(userRepository);
//        mainAPIs = new MainAPIs(userService);
//    }


    @Test
    void contextLoads() {
    }
//    @Test
//   public void testSignIn(){
//        User testUser = new User();
//        testUser.setUsername("malma");
//        testUser.setPassword("abcdef");
//        when(userService.getUserByUsername("malma")).thenReturn(testUser);
//
//        Map<String,String> credentials = new HashMap<>();
//        credentials.put("username",testUser.getUsername());
//        credentials.put("password", testUser.getPassword());
//        ResponseEntity<String> response = mainAPIs.signIn(request,credentials);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("success sign in " + testUser.getUsername(), response.getBody());
//
//        HttpSession s = request.getSession();
//        User u = (User)s.getAttribute("currentUser");
//
//
//   }
}
