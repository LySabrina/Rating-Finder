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
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@RunWith(MockitoJUnitRunner.class)
@SpringBootTest

public class MainAPIsTest {

    @InjectMocks
    private MainAPIs mainAPIs;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession session;
    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        UserService userService = new UserService(userRepository);
        mainAPIs = new MainAPIs(userService);
    }

    @Test
    void testSignIn() {

        // Mocking the HttpServletRequest and HttpSession
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpSession session = Mockito.mock(HttpSession.class);
        Mockito.when(request.getSession()).thenReturn(session);

        User testUser = new User();
        testUser.setUsername("malma");
        testUser.setPassword("abcdef");

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));

        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", testUser.getUsername());
        credentials.put("password", testUser.getPassword());

        ResponseEntity<String> response = mainAPIs.signIn(request, credentials);

        assertEquals(HttpStatus.OK, ((ResponseEntity<?>) response).getStatusCode());
        assertEquals("success sign in " + testUser.getUsername(), response.getBody());
    }
}
