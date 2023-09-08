# Rating Finder
Users enter a product-name which our application will scrape data from existing review sites (ex. HIFI, TrustedReviews).

Scraping the data will provide the pros and cons listed by these review sites. 

Additionally, YoutubeData API is used to fetch review-videos releated to the product

## Future implementation
Scrape the text for each category by the review site (ex. Display, Sound, Performance) and summarize the text and provide 
this summarized text to the user. Possible accomplish by using ChatGPT API or attempt to summarize the text yourself by using 
some Transformer model (ex. Hugging Face Transformer and Hugging Faces Models)

## How to Run 
1) Create a file called, "application-dev.properties" inside the directory: src/main/resources/
2) Input the API KEY in this file

Run the main program inside the following:
- TrustedReviewScrape
- HIFIScrape

When it's running, enter a product name. Then the pros and cons from that site will be displayed (it's web scraped data)