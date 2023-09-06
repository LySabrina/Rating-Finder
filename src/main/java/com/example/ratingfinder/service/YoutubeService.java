package com.example.ratingfinder.service;

import com.example.ratingfinder.config.YoutubeConfigProperties;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Value;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Uses YouTube API to fetch videos regarding the inputted search query
 */

@Service
public class YoutubeService {

    //FIND SOME WAY TO INJECT THIS KEY
    private static String DEVELOPER_KEY  = "";

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    /**
     * Build and return an authorized API client service.
     *
     * @return an authorized API client service
     * @throws GeneralSecurityException, IOException
     */
    public YouTube getService() throws GeneralSecurityException, IOException {
        System.out.println(DEVELOPER_KEY);

        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new YouTube.Builder(httpTransport, JSON_FACTORY, null)
                .setApplicationName("SCRAPER")
                .build();
    }

    public List<SearchResult> get(String productName) {

        try{
            YouTube youtubeService = getService();
            // Define and execute the API request
            List<String> part = new ArrayList<>();
            List<String> type = new ArrayList<>();
            part.add("snippet");
            type.add("video");
            YouTube.Search.List request = youtubeService.search()
                    .list(part);
            SearchListResponse response = request.setKey(DEVELOPER_KEY)
                    .setMaxResults(3L)
                    .setQ(productName + " review")
                    .setType(type)
                    .execute();
            List<SearchResult> items = response.getItems();
            for(SearchResult sr: items){
                String videoId = sr.getId().getVideoId();
                System.out.println(videoId);
            }

            return items;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args)throws GeneralSecurityException, IOException, GoogleJsonResponseException{
        YoutubeService youtubeService = new YoutubeService();
        Scanner input = new Scanner(System.in);
        while(input.hasNextLine()){
            String productName = input.nextLine();
            List<SearchResult> rs = youtubeService.get(productName);
            System.out.println("Enter");
        }
//        List<SearchResult> rs = service.get("sony wf-1000xm4");
    }
}

