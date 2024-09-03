# Rating Finder
This is an application that scrapes data from WhatHifi and Techradar. From the scraped product data and articles, this application uses 
OpenAI API to summarize the article. Thus this application is a central website that contains scraped product and their summarized reviews.

Additionally, users are able to make their own reviews, save product, and view the product information. 

# How to Run
In order to run, you will need an OpenAI API key. Once you obtain your key, go to the resources/application-dev.properties file and add the following:
openai-api-key = <YOUR KEY> 

In your application.properties file, make sure this line: app.db-init=true, is true.

After that, ensure you have a MySQL database called Rating_Finder up and running. 
Once you have both of these components, run the RatingFinderApplication file. There, products and their reviews will be scraped. OpenAI API will also be called
to get the summarized review to be saved to your database.

Run this program for about 10 minutes then stop. Go to your application.properties file and change the line to this: app.db-init=false. This is to prevent
scraping same data again and not waste the OpenAI API credits. 

Once all of this is done, you may now start your front-end. GO to the the front-end folder and run the command: npm start