package com.example.ratingfinder.config;

import com.google.api.client.util.Value;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
public class YoutubeConfigProperties {
    @Value("${youtube.api-key}")
    private String apiKey;

    public String getApiKey(){
        return apiKey;
    }

}
