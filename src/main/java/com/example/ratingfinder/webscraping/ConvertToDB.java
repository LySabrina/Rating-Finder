package com.example.ratingfinder.webscraping;

import com.example.ratingfinder.models.Product;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.boot.configurationprocessor.json.JSONArray;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ConvertToDB {

    public static List<Product> readJSONToDB(){
        List<Product> products = new ArrayList<Product>();

        try{
            JsonParser p = new JsonParser();
            JsonArray arr = (JsonArray) p.parse(new FileReader("/Users/sabrinaly/Desktop/Rating-Finder/src/main/resources/phone_data.json"));
            for(Object o : arr){
                JsonObject phone = (JsonObject) o;
                JsonElement brandProp=  phone.get("Brand");
                String brand = brandProp.getAsString();
                String model = phone.get("Model").getAsString();
                double price = phone.get("Price").getAsDouble();

                Product product = new Product();
                product.setName(model);
                product.setBrand(brand);
                product.setPrice(price);
                product.setType("Phone");
                File placeholder = new File("src/main/resources/images/placeholder.jpeg");
                byte[] imgArr = Files.readAllBytes(placeholder.toPath());
                product.setImage(imgArr);


                products.add(product);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return products;
    }

    public static void main(String[] args){
        List<Product> p = readJSONToDB();
        System.out.println(p.size());
    }
}
