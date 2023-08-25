package com.example.ratingfinder.service;

import org.jsoup.nodes.Document;

public interface ScrapeInterface {
    public void scrape(Document reviewPage);

    public Document crawl(String productName);
}
