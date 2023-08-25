package com.example.ratingfinder.controller;

import com.example.ratingfinder.service.YoutubeService;
import com.google.api.services.youtube.model.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
public class YoutubeController {
    @Autowired
    private YoutubeService youtubeService;

    @GetMapping("/videos")
    public List<SearchResult> getVideos(@RequestParam String productName){
        return youtubeService.get(productName);
    }
}
