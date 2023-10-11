package com.example.ratingfinder.utilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

@Component
public class ChatGPT {
    private static final String url = "https://api.openai.com/v1/chat/completions";

    @Value("${openai-api-key}")
    private  String API_KEY ;
    private static final String model = "gpt-3.5-turbo";


    public String chatGPT(String prompt) {
    String url = "https://api.openai.com/v1/chat/completions";

    String model = "gpt-3.5-turbo";

    try {
        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
        connection.setRequestProperty("Content-Type", "application/json");

        // The request body
        String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}]}";
        connection.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(body);
        writer.flush();
        writer.close();

        if(connection.getResponseCode() == 200){
            // Response from ChatGPT
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            StringBuffer response = new StringBuffer();

            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            // calls the method to extract the message.
            return extractMessageFromJSONResponse(response.toString());
        }
        else{
            System.out.println("FAILED TO SUMMARIZE W/CHATGPT");
            return null;
        }


    } catch (IOException e) {
        throw new RuntimeException(e);
    }
}

    public String extractMessageFromJSONResponse(String response) {
        int start = response.indexOf("content")+ 11;

        int end = response.indexOf("\"", start);

        return response.substring(start, end);
    }



    public static void main(String[] args) {
//        summarizeText("Text");
//        ChatGPT c = new ChatGPT();
//        System.out.println(c.chatGPT("What is samsung"));



    }

}
