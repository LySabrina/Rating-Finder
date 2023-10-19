package com.example.ratingfinder.utilities;

import com.example.ratingfinder.Repository.ProductRepository;
import com.example.ratingfinder.Repository.ReviewRepository;
import com.example.ratingfinder.models.Product;
import com.example.ratingfinder.models.Review;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Component
public class TechRadarScrape {
    private final String PHONE_URL = "https://www.techradar.com/phones/reviews/page/2";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36";
    private static Map<String, String> HTTP_HEADERS = new HashMap<>(){{put("Accept-Language", "*");put("Referer", "https://www.google.com/");}};

    private ProductRepository productRepository;
    private ReviewRepository reviewRepository;
    private ChatGPT chatGPT;

    @Autowired
    public TechRadarScrape(ProductRepository repository, ReviewRepository reviewRepository, ChatGPT chatGPT){
        this.productRepository = repository;
        this.reviewRepository = reviewRepository;
        this.chatGPT = chatGPT;
    }
    //loop through list, get each product and scrape it. Get the product name, price and category review
    public Document crawl(){
        try{
            Document doc = Jsoup.connect(PHONE_URL).userAgent(USER_AGENT).headers(HTTP_HEADERS).get();
            //scrape the first page which is the current doc
            scrape(doc);
            Element pages_container = doc.selectFirst("div.flexi-pagination");
            Elements links = pages_container.select("a");

            //Loop through each page and pass the documnt to scrape method
            for(Element link: links){
                System.out.println("ON PAGE - " + link.text());
                if(link.text().toLowerCase().equals("archives")){
                    continue;
                }
                Document nextPage = Jsoup.connect(link.attr("href")).userAgent(USER_AGENT).headers(HTTP_HEADERS).get();

                scrape(nextPage);
            }

            return doc;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void scrape(Document doc) throws IOException {
        Element reviewList = doc.selectFirst("div.listingResults");
        Elements reviewsArticle = reviewList.children();

        //On the page, loop thrugh the article list
        for(Element a : reviewsArticle){
            String reviewTitle = a.child(0).attr("aria-label");
            int index = reviewTitle.indexOf("review");
            if(index == -1){
                continue;
            }

            //Setting the basic information about the Product
            Product product = new Product();
            String productName = reviewTitle.substring(0, index);
            String brand = productName.substring(0, productName.indexOf(" "));
            product.setName(productName);
            product.setBrand(brand);
            product.setType("Phone");

            //Download the image of each product
            Element picture_container = a.selectFirst("div.image-remove-reflow-container").child(0).child(1);
            String imgLink = picture_container.attr("data-srcset").substring(0, picture_container.attr("data-srcset").indexOf(" "));
            Connection.Response response = Jsoup.connect(imgLink).ignoreContentType(true).execute();
            InputStream inputStream = response.bodyStream();
            byte[] imgBytes = inputStream.readAllBytes();
            product.setImage(imgBytes);

            //Scraping the Review Article of each product
            String prod_review_link = a.child(0).attr("href");
            Document review_article = Jsoup.connect(prod_review_link).userAgent(USER_AGENT).headers(HTTP_HEADERS).get();

            Elements h2s = review_article.select("h2.article-body__section");

            //Looping through the article category
            for(Element h2 : h2s){
                HashMap<String, String> categoryText= new HashMap<String, String>();
                if(!h2.text().contains("review") || h2.text().toLowerCase().contains("minute") || h2.text().toLowerCase().contains("score card") || h2.text().toLowerCase().contains("also consider")){
                    continue;
                }
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
                                double price = Double.parseDouble(start.substring(1).replaceAll(",", "").replaceAll("/", ""));
                                product.setPrice(price);
                            }
                            else{
                                double price = Double.parseDouble(start.substring(1, spaceIndex).replaceAll(",", "").replaceAll("/", ""));
                                product.setPrice(price);
                            }
                        }
                    }
                }

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

                System.out.println("CATEGORY: " +  category + " TEXT: " + stringBuilder.toString());
                String summary =chatGPT.chatGPT(stringBuilder.toString());
                System.out.println("CHATGPT SUMMARY - " + summary);
                r.setCategory(category);
                r.setSummary(summary);
                Product p = productRepository.save(product);
                r.setProduct(p);
                reviewRepository.save(r);
                System.out.println("SAVED PRODUCT");
            }
            System.out.println(product.getName());
            inputStream.close();

            //Optional - Convert byte[] to base64
//            String base64EncodedImage = Base64.getEncoder().encodeToString(imgBytes);
//            System.out.println("Base64 Encoded Image Data:");
//            System.out.println(base64EncodedImage);

        }
    }

    public static void main(String[] args){
//            TechRadar techRadar = new TechRadar();
//            techRadar.crawl();
    }

}
