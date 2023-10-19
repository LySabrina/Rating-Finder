package com.example.ratingfinder.utilities;

import com.example.ratingfinder.models.Product;
import com.example.ratingfinder.models.Review;
import com.example.ratingfinder.service.ProductService;
import com.example.ratingfinder.service.ReviewService;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class TechRadar {
    //check if the product already exist. If it does then it is not null. The id will be given. Then write a review of it.
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36";
    private static Map<String, String> HTTP_HEADERS = new HashMap<>(){{put("Accept-Language", "*");put("Referer", "https://www.google.com/");}};

    private final ProductService productService;
    private final ChatGPT chatGPT;
    private final ReviewService reviewService;
    public TechRadar(ProductService productService, ChatGPT chatGPT, ReviewService reviewService) {
        this.productService = productService;
        this.chatGPT = chatGPT;
        this.reviewService = reviewService;
    }


    public void crawl(String startingUrl, String type){
        try{
            Document doc = Jsoup.connect(startingUrl).headers(HTTP_HEADERS).userAgent(USER_AGENT).get();
            scrapePage(doc, type);
            Element pages_container = doc.selectFirst("div.flexi-pagination");
            Elements links = pages_container.select("a");

//            //Loop through each page and pass the documnt to scrape method
            for(Element link: links){
                System.out.println("ON PAGE - " + link.text());
                if(link.text().toLowerCase().equals("archives")){
                    continue;
                }
                Document nextPage = Jsoup.connect(link.attr("href")).userAgent(USER_AGENT).headers(HTTP_HEADERS).get();

                scrapePage(nextPage, type);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void scrapePage(Document doc, String type) throws IOException {
        Element reviewList = doc.selectFirst("div.listingResults");
        Elements reviewsArticle = reviewList.children();

        //On the page, loop thrugh the article list
        for (Element a : reviewsArticle) {
            String reviewTitle = a.child(0).attr("aria-label");
            int index = reviewTitle.indexOf("review");
            if (index == -1) {
                continue;
            }

            //Setting the basic information about the Product
            Product product = new Product();
            //-1 to get rid of extra space or colon at the end
            String productName = reviewTitle.substring(0, index -1);
            String brand = productName.substring(0, productName.indexOf(" "));
            if(brand.equals("iPhone")){
                brand= "Apple";
            }

            System.out.println("PRODUCT NAME - " + productName);
            Product hasId = productService.getExistingProductID(productName);
            //it exist in the DB

            String prod_review_link = a.child(0).attr("href");
            Document review_article = Jsoup.connect(prod_review_link).userAgent(USER_AGENT).headers(HTTP_HEADERS).get();
            scrapeReviewPage(review_article);
            if(hasId != null){
                HashMap<String, String> categoryText = scrapeReviewPage(review_article);
                addReview(hasId, categoryText);
            }
            //product does not exist and must be added to the DB
            else{
                Element picture_container = a.selectFirst("div.image-remove-reflow-container").child(0).child(1);
                String imgLink = picture_container.attr("data-srcset").substring(0, picture_container.attr("data-srcset").indexOf(" "));
                Connection.Response response = Jsoup.connect(imgLink).ignoreContentType(true).execute();
                InputStream inputStream = response.bodyStream();
                byte[] imgBytes = inputStream.readAllBytes();

                  double price = getPrice(review_article);
                    product.setPrice(price);
                    product.setName(productName);
                    product.setBrand(brand);
                    product.setType(type);
                product.setImage(imgBytes);
                    Product savedProduct = productService.save(product);

               HashMap<String, String> categoryText = scrapeReviewPage(review_article);
               addReview(savedProduct, categoryText);

            }
        }
    }

    public HashMap<String, String> scrapeReviewPage(Document review_article){
        HashMap<String, String> categoryText = new HashMap<>();
        Elements h2s = review_article.select("h2.article-body__section");

        for(Element h2:h2s){
            if(!h2.text().contains("review") || h2.text().toLowerCase().contains("minute") || h2.text().toLowerCase().contains("score card") || h2.text().toLowerCase().contains("also consider")){
                continue;
            }
            else{
                Element current = h2.nextElementSibling();
                StringBuilder stringBuilder = new StringBuilder();

                //Getting the category paragraph
                Review r = new Review();
                r.setCompany("TechRadar");
                while(current != null && !current.tagName().equals("h2") ){
                    if(current.tagName().equals("p")){

                        stringBuilder.append(current.text());
                    }
                    current = current.nextElementSibling();
                }
                String category = h2.text().substring(h2.text().indexOf(":") + 1);

                categoryText.put(category, stringBuilder.toString());
            }
        }
        return categoryText;
    }

    public void addReview(Product p , HashMap<String, String> categoryText){

        Set<String> keys = categoryText.keySet();
        //Creating Review objects that has summarized text
        for(String category : keys){
            String text = categoryText.get(category);
            System.out.println("ORIGINAL TEXT - " + text);
            String summarizedText = chatGPT.chatGPT ("Summarize this in a few sentences: " + text);
                System.out.println("SUMMARIZED TEXT = " + summarizedText);
            Review review = new Review();
            review.setCompany("TechRadar");
            review.setCategory(category);
            review.setProduct(p);
            review.setSummary(summarizedText);
//            review.setSummary(text);
            reviewService.save(review);


        }
    }
    public double getPrice(Document review_article){
        Elements h2s = review_article.select("h2.article-body__section");

        double price = 0;
        for(Element h2: h2s){
            if(h2.text().toLowerCase().contains("price")){

                Element ul = h2.siblingElements().select("ul").first();

                if(ul == null){
                    continue;
                }
                Elements lis = ul.children();

                for(Element li : lis){
                    if(li.text().toLowerCase().contains("$")){
                        int dollarIndex = li.text().indexOf("$");
                        String start = li.text().substring(dollarIndex);
                        System.out.println("START " + start );
                        int spaceIndex = start.indexOf(" ");
                        if(spaceIndex == -1){
                             price = Double.parseDouble(start.substring(1).replaceAll(",", "").replaceAll("/", ""));

                        }
                        else{
                            price = Double.parseDouble(start.substring(1, spaceIndex).replaceAll(",", "").replaceAll("/", ""));

                        }
                    }
                }
            }
        }
        System.out.println("PRICE : " + price );
        return price;
    }
    public static void main(String[] args){
//            TechRadar r = new TechRadar();
//            try{
//                Document review_article = Jsoup.connect("https://www.techradar.com/phones/iphone/iphone-15-plus-review").userAgent(USER_AGENT).headers(HTTP_HEADERS).get();
//
//                r.getPrice(review_article);
//            }
//            catch (Exception e){
//                e.printStackTrace();
//            }




    }

}
