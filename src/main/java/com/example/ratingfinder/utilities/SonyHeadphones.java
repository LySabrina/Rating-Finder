package com.example.ratingfinder.utilities;

import com.example.ratingfinder.models.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Fetch all available Sony Headphones from Official Sony Website
 * We should still allow the option to search products since some headphones/product might be discontinued
 * How to format the products into the database?
 */
public class SonyHeadphones {
    private static final String USER_AGENT =  "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36";

    private static Map<String, String> HTTP_HEADERS = new HashMap<>(){{put("Accept-Language", "*");put("Referer", "https:google.com");}};

    private static ArrayList<Product> products = new ArrayList<>();

    /**
     * Crawl on web pages and pass to the scraper function
     */
    public static void crawl(){
        try{
            Document doc = Jsoup.connect("https://electronics.sony.com/audio/headphones/c/all-headphones?sortCode=relevance&currentPage=99").headers(HTTP_HEADERS).userAgent(USER_AGENT).get();
//            Elements headphones = doc.select(".custom-product-grid-item__info > p");
            Elements headphones = doc.select(".custom-product-grid-item");
            Elements headphones_types = doc.select(".multi-select").get(2).select(".custom-facet__list__price > a");


            for(Element type : headphones_types){
                String type_name = type.child(0).text();
                String href = type.attr("href");
                String link ="https://electronics.sony.com/" + href + "&currentPage=10";
                System.out.println(link);
                HTTP_HEADERS.put("Referer", "https://electronics.sony.com/audio/headphones/c/all-headphones?query=:relevance:snaAllCategories:all-headphones"); //Updates the HTTP headers to state we are coming from the all headphone pages
                Document typePage = Jsoup.connect(link).userAgent(USER_AGENT).headers(HTTP_HEADERS).get();
                scrape(typePage, type_name);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Scraping the product and creating a Product object
     * @param doc the HTML Document to scrape from
     * @param type_name The type associated with the product (ex. In-ear, Headband, etc.)
     */
    public static void scrape(Document doc, String type_name) {
        Elements headphones = doc.select(".custom-product-grid-item");

        for (Element e : headphones) {
                String productName = e.select(".custom-product-grid-item__product-name").text();
                productName = productName.substring(productName.indexOf(" "));      //Get rid of the "Model: " part

                String priceStr = e.select(".custom-product-grid-item__price").text();
                if(priceStr.isEmpty()){
                    priceStr = e.select(".custom-product-grid-item__price__actual").text();
                }
                priceStr = priceStr.replaceAll(",",""); //if a comma exist, remove it
                double price = Double.parseDouble(priceStr.substring(priceStr.indexOf("$")+1));   //convert from String to double and get rid of the dollar sign


                  Product product = new Product();
                  product.setName(productName.replaceAll(" ", ""));
                  product.setPrice(price);
                  product.setBrand("Sony");
                  product.setType(type_name);
                  products.add(product);    //adding to an array



        }

    }

    public static ArrayList<Product> getSonyProducts (){
        SonyHeadphones.crawl();
        return products;
    }

    public static void main (String[] args){
        getSonyProducts();
        System.out.println(products.toString());

    }

}
