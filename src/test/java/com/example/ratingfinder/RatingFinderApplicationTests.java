package com.example.ratingfinder;

import com.example.ratingfinder.Repository.UserRepository;
import com.example.ratingfinder.controller.MainAPIs;
import com.example.ratingfinder.models.Product;
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
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.*;

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
        List<String> stringList = Arrays.asList("macbook air Apple laptop","macbook pro Apple laptop","macbook Apple laptop "," iphone Apple smartphone"," ipad pro Apple tablet","Imac Apple desktop"," mac pro Apple desktop",
                "mac");
        String example = "macbook";

        String[] ea = new String[]{"iphone"};
        String [] sa= new String[]{"imac","iphone15","pro","max"};
        for(String s: sa)
        {
            for(String e: ea)
            {
                levenshteinDistance(s,e,4);
            }

        }
        Collections.sort(stringList,Comparator.comparing(s-> levenshteinDistance(s,example,4)));
        System.out.println(stringList);
    }
    private static int calculateSimilarity(String str1, String str2) {
        int similarity = LevenshteinDistance.getDefaultInstance().apply(str1, str2);

        return similarity;
    }

    private int levenshteinDistance(String s1, String s2, int weight) {

        int[][] dp = new int[s1.length() + 1][s2.length() + 1];


        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    dp[i][j] = min(dp[i - 1][j - 1] + costOfSubstitution(s1.charAt(i - 1), s2.charAt(j - 1)),
                            dp[i - 1][j] + 1,
                            dp[i][j - 1] + 1);
                }
            }
        }

        int similarity = dp[s1.length()][s2.length()];
        similarity = s2.length()-similarity;
        return similarity * weight;
    }

    private static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    private static int min(int... numbers) {
        return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
    }
//    public int wordSimilarity (String s1,String s2)
//    {
//        String[] words1 = Arrays.stream(s1.split("\\s+")).filter(word->!word.isEmpty()).toArray(String[]::new);
//        String[] words2 = Arrays.stream(s2.split("\\s+")).filter(word->!word.isEmpty()).toArray(String[]::new);
//
//        int similarity = 0;
//        int commonWords = Math.min(words1.length, words2.length);
//        //swap to use the least length
////        if(words1.length<words1.length)
////        {
////            String [] temp = words2;
////            words2 =words1;
////            words1 = temp;
////        }
//        for(int i=0;i<commonWords;i++)
//        {
//            for(int j =0;j<commonWords;j++)
//            {
//                if(words1[i].equalsIgnoreCase(words2[j]))
//                {
//                    similarity++;
//                }
//            }
//        }
//        return -similarity; // Negative to sort in descending order of similarity
//
//    }
    private static int wordOrderSimilarity(String s1, String s2) {
        String[] words1 = s1.split(" ");
        String[] words2 = s2.split(" ");

        int similarity = 0;
        int commonWords = Math.min(words1.length, words2.length);

        for (int i = 0; i < commonWords; i++) {
            if (words1[i].equalsIgnoreCase(words2[i])) {
                similarity++;
            } else {
                break; // Stop counting similarity when a mismatch is found
            }
        }

        return -similarity; // Negative to sort in descending order of similarity
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
