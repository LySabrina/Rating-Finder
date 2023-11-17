package com.example.ratingfinder.controller;

import com.example.ratingfinder.models.Product;
import com.example.ratingfinder.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class SearchingEngine {
    @Autowired
    public JdbcTemplate jdbcTemplate;

    private final ReviewService reviewService;

    private final ProductService productService;
    private final UserService userService;

    private final UserReviewService userReviewService;

    private final ImageService imageService;
    public SearchingEngine(ReviewService reviewService, ProductService productService, UserService userService, UserReviewService userReviewService, ImageService imageService){
        this.reviewService = reviewService;
        this.productService = productService;
        this.userService = userService;
        this.userReviewService = userReviewService;
        this.imageService = imageService;
    }
//searching controller
    @GetMapping("/search/typeFilter")
    public ResponseEntity<List<Product>> typeFilter(@RequestParam String type, @RequestParam List<Product> productList){
        List<Product> resulList = new ArrayList<>();
        for(Product p: productList)
        {
            if(p.getType().equalsIgnoreCase(type)){
                resulList.add(p);
            }
        }
        return ResponseEntity.ok(resulList);
    }
    @GetMapping("/search/priceFilter")
    public ResponseEntity<List<Product>> priceFilter(@RequestParam double minPrice,@RequestParam double maxPrice, @RequestParam List<Product> productList){
        List<Product> resulList = new ArrayList<>();
        for(Product p: productList)
        {
            if(p.getPrice()>= minPrice && p.getPrice()<= maxPrice){
                resulList.add(p);
            }
        }
        return ResponseEntity.ok(resulList);
    }

    @GetMapping("/search/brandFilter")
    public ResponseEntity<List<Product>> brandFilter(@RequestParam String brand, @RequestParam List<Product> productList){
        List<Product> resulList = new ArrayList<>();
        for(Product p: productList)
        {
            if(p.getBrand().equalsIgnoreCase(brand)){
                resulList.add(p);
            }
        }
        return ResponseEntity.ok(resulList);
    }

    @GetMapping("/searchByAttributes")
    public ResponseEntity<List<Product>> searchByAttributes(@RequestParam String name,
                                                            @RequestParam String brand,
                                                            @RequestParam int minPrice,
                                                            @RequestParam int maxPrice,
                                                            @RequestParam String type){
       List<Product> productList =  keywordSearching(name).getBody();
       int command =0;
       while(productList!=null && productList.size()>0){
           if(command==0)
           {
               productList = brandFilter(brand,productList).getBody();
               command+=1;
           }else if(command==1){
               productList = priceFilter(minPrice,maxPrice,productList).getBody();
               command+=1;
           }else if(command ==2){
               productList = typeFilter(type,productList).getBody();
               command+=1;
           }else {
               break;
           }
       }
       if(productList==null || productList.size()==0)
       {
           return ResponseEntity.notFound().build();
       }
       else {
           Collections.sort(productList,Comparator.comparing(p-> sortByRating(p)));
           return ResponseEntity.ok(productList);
       }

    }

    public int sortByRating(Product p){
        return -p.getRating();
    }







    @GetMapping("/search/keyword")
    public ResponseEntity<List<Product>> keywordSearching(@RequestParam String keyword)
    {
        if( keyword.length()==0)
        {
            return ResponseEntity.notFound().build();
        }

        //get the total amount of our product
        String sqlGetTotal = "select count(prod_id) from product";
        Integer amount = 0;
        amount = jdbcTemplate.queryForObject(sqlGetTotal,Integer.class);
        //prevent nullpointerexception
        int amountRe = (amount!=null) ? amount:0;
        int minResult = 2;
        int maxResult = 10;
        //make it clear without punct
        List<Product> resultList = new ArrayList<>();
        //filter redundant space
        String []words = Arrays.stream(keyword.split("\\s+")).filter(word ->!word.isEmpty()).toArray(String[]::new);
        //for normal searching entry
        if(words.length>7)
        {
            HashSet<String> wordsSet = new HashSet<>(Arrays.asList(words));
            words = wordsSet.toArray(new String[0]);
        }
        if(words.length<7 && words.length>=1)
        {
            resultList = searchByWord(keyword, resultList,amountRe/3);

            resultList = productsSort(resultList,keyword,resultList.size());
            //if too less product
            if(resultList.size()< 10 )
            {
                resultList=basicFuzzySearch(keyword,resultList,10);

            }

        }else {
            resultList = basicFuzzySearch(words[0],resultList,10);
        }






        return  ResponseEntity.ok(resultList);

    }
    //algorithm tools
    public List<Product> basicFuzzySearch(String keyword, List<Product> resultList,int expectation)
    {
        String agKeyword;
        agKeyword = aggregation(keyword);
        String original = keyword;
        keyword = agKeyword;
        if(keyword.length()>20)
        {
            keyword = agKeyword.substring(0,20);
        }

        List<Product> fuzzyList = new ArrayList<>();
        int diff = expectation - resultList.size();

        do{
            //basic fuzzy search
            String sqlForList = "SELECT *\n" +
                    "FROM product\n" +
                    "WHERE \n" +
                    "    (LOWER(CONCAT(product_brand, ' ', product_type, ' ', product_name)) LIKE '% "+keyword+"%' OR\n" +
                    "     LOWER(product_brand) LIKE '% "+keyword+"%' AND LOWER(product_type) LIKE '% "+keyword+"%' AND LOWER(product_name) LIKE '% "+keyword+"%' OR\n" +
                    "     LOWER(product_brand) LIKE '% "+keyword+"%' OR\n" +
                    "     LOWER(product_type) LIKE '% "+keyword+"%' OR\n" +
                    "     LOWER(product_name) LIKE '% "+keyword+"%' OR\n" +
                    "     LOWER(CONCAT(product_brand, product_type, product_name)) LIKE '%"+keyword+"%' OR\n" +
                    "     LOWER(CONCAT(product_brand, product_name, product_type)) LIKE '%"+keyword+"%' OR\n" +
                    "     LOWER(CONCAT(product_name, product_type, product_brand)) LIKE '% "+keyword+"%' OR\n" +
                    "     LOWER(CONCAT(product_name, product_brand, product_type)) LIKE '% "+keyword+"%' OR\n" +
                    "     LOWER(CONCAT(product_type, product_name, product_brand)) LIKE '% "+keyword+"%' OR\n" +
                    "     LOWER(CONCAT(product_type, product_brand, product_name)) LIKE '% "+keyword+"%' \n" +
                    "     );";
            List<Map<String,Object>> objectList = new ArrayList<>();
            objectList = jdbcTemplate.queryForList(sqlForList);

            List<Product> productList = productListConverter(objectList);
            if(productList.size()!=0)
            {
                for(Product p : productList)
                {
                    if(!resultList.contains(p) && resultList.size()<expectation && !fuzzyList.contains(p))
                    {
                        fuzzyList.add(p);
                    }
                }

            }
            if(fuzzyList.size()>=diff)
            {
                break;
            }
            keyword = keyword.substring(0,keyword.length()-1);

        }while(keyword.length()>=1);

        fuzzyList =productsSort(fuzzyList,original,diff);
        resultList.addAll(fuzzyList);

        return resultList;
    }

    public List<Product> productsSort(List<Product> pList, String keyword, int amount)
    {
        //test
//        List<Product> testP =new ArrayList<>();
//        for(Product p: pList)
//        {
//            if(p.getProd_id() == 49 || p.getProd_id()==59|| p.getProd_id()==35)
//            {
//                testP.add(p);
//            }
//        }
        //sort it
        Collections.sort(pList,Comparator.comparing(s-> wordSimilarity(s,keyword)));

        //return first amount
        return pList.subList(0,Math.min(pList.size(),amount));

    }
    public int wordSimilarity (Product p,String s2)
    {
        String s1 = p.getName()+" "+p.getBrand()+" "+p.getType();
        String[] words1 = Arrays.stream(s1.split("\\s+")).filter(word->!word.isEmpty()).toArray(String[]::new);
        String[] words2 = Arrays.stream(s2.split("\\s+")).filter(word->!word.isEmpty()).toArray(String[]::new);

        Set<String> word2Set = new HashSet<>(Arrays.asList(words2));
        words2 = word2Set.toArray(new String[0]);

        int similarity = 0;
        int commonWords = Math.min(words1.length, words2.length);
        String[] nameArr = Arrays.stream(p.getName().split("\\s+")).filter(word->!word.isEmpty()).toArray(String[]::new);
        //equal words weight and compare
        for(int i=0;i<words2.length;i++)
        {
            for(int j =0;j<words1.length;j++)
            {
                if(words1[j].equalsIgnoreCase(words2[i]))
                {
                    similarity+=(words1[j].length())*20;
                }

            }
            for(String name : nameArr)
            {
                similarity+=levenshteinDistance(name.substring(0,Math.min(s2.length(),name.length())),words2[i],3);
            }
        }
        //similarity compare

        similarity+=levenshteinDistance(p.getBrand(),s2,2);
        similarity+=levenshteinDistance(p.getType(),s2,1);



        return -similarity; // Negative to sort in descending order of similarity

    }
    private int levenshteinDistance(String s1, String s2,int weight) {
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
        return similarity*weight;
    }
    private static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    private static int min(int... numbers) {
        return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
    }

    //tool convert a List<Map<String,object>> to product list;
    public List<Product> productListConverter(List<Map<String,Object>> productList)
    {
        List<Product> resultList = new ArrayList<>();
        for(Map<String,Object> map: productList)
        {
            Product newP = new Product ( (Integer) map.get("prod_id"), map.get("product_name").toString(),map.get("product_brand").toString(),(Double) map.get("price"),map.get("product_type").toString(),(byte[]) map.get("image"),(Integer)map.get("rating"));
            resultList.add(newP);
        }

        return resultList;
    }

    //a search method that search product word by word.
    public List<Product> searchByWord(String keyword,List<Product> resultList,int amount)
    {

        String[] words = Arrays.stream(keyword.split("\\s+")).filter(word ->!word.isEmpty()).toArray(String[]::new);
        //to prevent frequently query the sql to cause high concurency exception, we have to controller the searching times below 10 times
        if(words.length<5 && words.length>=1)
        {
            for(String word : words)
            {
                //String sql = "select* from product where(product_type LIKE '%"+word+"%' OR product_name LIKE '%"+word+"%' OR product_brand LIKE '%"+word+" %' ) ";
                List<Map<String,Object>> productList = new ArrayList<>();
                String sql = "SELECT * FROM product WHERE product_type LIKE ? OR product_name LIKE ? OR product_brand LIKE ? ORDER BY \n" +
                        "  CASE\n" +
                        "    WHEN product_name = ? THEN 1\n" +
                        "    WHEN product_brand = ? THEN 2\n" +
                        "    WHEN product_type = ? THEN 3\n"+
                        "    WHEN product_name LIKE ? THEN 3\n" +
                        "    WHEN product_brand LIKE ? THEN 4\n" +
                        "    WHEN product_name LIKE ? THEN 5\n" +
                        "    WHEN product_brand LIKE ? THEN 6\n"+
                        "    ELSE 7\n" +
                        "  END; ";
                productList = jdbcTemplate.queryForList(sql, "%" + word + "%", "%" + word + "%", "%" + word + "%",word,word,word,"%" + word + "%","%" + word + "%","%" + word + "%","%" + word + "%");
                //productList = jdbcTemplate.queryForList(sql);
                resultList.addAll(productListConverter(productList));
            }
        }

          List<Product> returnList = new ArrayList<>();
          for(Product p: resultList){
              if (!returnList.contains(p))
              {
                  returnList.add(p);
              }
          }

          return returnList;
    }


    public String aggregation(String entry)
    {
        // Define a regular expression for punctuation, spaces, and newlines
        String regex = "[\\p{Punct}\\s\n]";
        // Use the regular expression to replace matches with an empty string
        return entry.replaceAll(regex, "");
    }







}
