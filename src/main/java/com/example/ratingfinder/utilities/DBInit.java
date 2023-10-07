package com.example.ratingfinder.utilities;

import com.example.ratingfinder.models.Product;
import com.example.ratingfinder.models.Review;
import com.example.ratingfinder.Repository.ProductRepository;
import com.example.ratingfinder.Repository.ReviewRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Component
@ConditionalOnProperty(name="app.db-init", havingValue ="true")
public class DBInit implements CommandLineRunner {


    private ProductRepository productRepository;


    private ReviewRepository reviewRepository;
    public DBInit(ProductRepository productRepository, ReviewRepository reviewRepository){
        this.productRepository = productRepository;
        this.reviewRepository = reviewRepository;
    }
    @Override
    public void run(String... args) throws Exception {
        reviewRepository.deleteAll();
        productRepository.deleteAll();

        //Saving Sony Headphone products to the database
        ArrayList<Product> prods = SonyHeadphones.getSonyProducts();
        productRepository.saveAll(prods);
        System.out.println("SAVED PRODUCTS");

        //Fetching all the Products recently added to the database
        List<Product> products = productRepository.findAll();

        for(Product product : products){

            //Contains the pair <CATEGORY, TEXT>
            HashMap<String, String> categoryText = TrustedReviewScrape.getReviews(product.getName());

            //If the article can not be for TrustedReviews, skip
            if(categoryText == null){
                continue;
            }

            Set<String> keys = categoryText.keySet();
            //Creating Review objects that has summarized text by TrustedReview
            for(String category : keys){
                String text = categoryText.get(category);
                String summarizedText = ChatGPT.chatGPT("Summarize this in a few sentences: " + text);
//                System.out.println("SUMMARIZED TEXT = " + summarizedText);
                Review review = new Review();
                review.setCompany("Trusted Review");
                review.setCategory(category);
                review.setProduct(product);
                review.setSummary(summarizedText);
                reviewRepository.save(review);

                System.out.println("REVIEW: " + review.toString() + " FOR PRODUCT - " + product.getName() + " w/ID: " + product.getProd_id());
            }
        }




    }
}
