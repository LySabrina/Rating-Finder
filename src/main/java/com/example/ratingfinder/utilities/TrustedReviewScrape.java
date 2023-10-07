package com.example.ratingfinder.utilities;

import com.google.api.client.util.Value;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class TrustedReviewScrape {

    private static String BASE_URL = "https://www.trustedreviews.com/reviews/iphone-x-review";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36";
    private static Map<String, String> HTTP_HEADERS = new HashMap<>(){{put("Accept-Language", "*");put("Referer", "https://www.whathifi.com/us");}};

    @Value("${apiKey}")
    private static String API_KEY ;

    public static Document crawl(String productName){
        try{

            String SEARCH_ENGINE_ID = "111cd93fc2e5d4b32";
            productName += " trusted reviews";
            productName = productName.replaceAll(" ", "%20");

            String base_url = "https://www.googleapis.com/customsearch/v1?key=" + API_KEY +"&cx=" + SEARCH_ENGINE_ID + "&num="+"2"+ "&q=" + productName;
            URL url = new URL(base_url);
            HttpURLConnection urlConnection =(HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            int status = urlConnection.getResponseCode();
            System.out.println("Response status: " + status);

            //Reading the HTTP Request Body
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;
            String json = "";

            while((inputLine = br.readLine()) != null){
                json += inputLine;
            }

            //Converting the String into a JSONObj
            //Getting the key, "items", which holds an array
            //Getting the first element in the array then getting the link

            JSONObject obj = new JSONObject(json);
            JSONArray arr = obj.getJSONArray("items");
            System.out.println("ITEMS ARR");
            JSONObject element = arr.getJSONObject(0);
            String link = element.getString("link");
            System.out.println("LINK: " + link);

            String subLink = link.substring(26);

            if(!subLink.toLowerCase().contains("review") || !link.contains("trustedreviews")){
                System.out.println("LINK DID NOT CONTAIN REVIEW NOR WAS FROM TRUSTREDREVIEW. RETURNING NULL");
                return null;
            }
            System.out.println("Contains review: " + link.contains("review"));
            Document doc = Jsoup.connect(link).userAgent(USER_AGENT).headers(HTTP_HEADERS).get();
            System.out.println("DOCUMENT:");

//            System.out.println(doc);      PRINTS OUT THE HTML DOCUMENT

            br.close();
            return doc;
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static HashMap<String, String> scrape(Document doc){
        Elements pros = doc.select("ul.product-pros-list").first().children();
        Elements cons = doc.select("ul.product-cons-list").first().children();
        System.out.println("PROS:");
        for(Element e : pros){
            System.out.print(e.text() + ", ");
        }


        System.out.println("\nCONS:");
        for(Element e: cons){
            System.out.print(e.text() + ", ");
        }

        System.out.println();
        System.out.println();

        Elements h2s = doc.select("h2#introduction ~ h2");  //get the name of each section
        //Gets the <p> from all the headings. Should be used to have Python SpaCy summarize the paragraphs

        HashMap<String, String> categoryText = new HashMap<>();


        System.out.print("FEATURE ARTICLE TEXT");
        for(Element e : h2s){
            System.out.println();
            if(e.text().compareTo("Latest deals") == 0){
                break;
            }
            String id = e.id();
            System.out.println("ID : " + id);
            Elements ps = doc.select("#"+id + " ~ p");
            System.out.println(ps.toString());

            StringBuilder stringBuilder = new StringBuilder();

//            String article = "";        //use the string to pass to a Python program to summarize the text
            for(Element p : ps){
                stringBuilder.append(p.text());
            }

            String text = stringBuilder.toString();
            System.out.println(stringBuilder.toString());
            categoryText.put(id, text);


        }
        return categoryText;

    }

    public static void main(String args[]) throws Exception{
        if(readAPIKey() == null){
            System.out.println("FAILED TO LOAD API KEY");
        }
        else{
            System.out.println("TRUSTED-REVIEW - ENTER PRODUCT NAME (EX. Iphone 14 pro)");
            String input = "";
            Scanner scan = new Scanner(System.in);

            while(scan.hasNextLine()){
                input = scan.nextLine();
                if(input.compareTo("") == 0){
                    System.out.println("Invalid product name");
                }
                else{
                    Document doc = crawl(input);
                    if(doc == null){
                        System.out.println("Product review could not be found");
                    }
                    else{
                        HashMap<String, String> results = scrape(doc);
                        for (String key : results.keySet()){
                            System.out.println("KEY=" + key);
                            System.out.println("SUMMARIZED PARAGRAPH: " + ChatGPT.chatGPT(results.get(key)));
                        }
                    }

                }

            }
        }
    }

    public static HashMap<String, String> getReviews(String productName){
        if(readAPIKey() == null){
            System.out.println("-- FAILED TO LOAD API KEY");
            System.exit(1);
        }
        Document doc = crawl(productName);
        //meaning it wasn't able to find an article for that product
        if(doc == null){
            return null;
        }
        else{
            HashMap<String, String> categoryText  = scrape(doc);
            return categoryText;
        }

    }
    public static String readAPIKey(){
        Properties properties = new Properties();

        try(FileInputStream input = new FileInputStream("src/main/resources/application-dev.properties")){
            properties.load(input);
            TrustedReviewScrape.API_KEY = properties.getProperty("apiKey");
            return properties.getProperty("apiKey");

        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

}