package com.mast.readup.integration;


import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.mast.readup.dto.GoogleBooksResponse;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Simple client for Google Books API.
 * It fetches the description of a book by title and author.
 */
@Component
public class GoogleBooksClient {

    private final RestTemplate restTemplate;
    private final String apiKey;

    /**
     * Constructor injects RestTemplate and API key.
     * @param restTemplate the HTTP client bean
     * @param apiKey the Google Books API key (optional)
     */
    public GoogleBooksClient(RestTemplate restTemplate,@Value("${google.books.api.key:}") String apiKey) {
        
        // Initialize RestTemplate and API key
        // API key can be empty if not configured    
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

     /**
     * Get book description by title and author.
     */
    @RateLimiter(name = "googleBooksLimiter")
    public Optional<String> fetchDescription(String title, String author) {
        try {
            String query = "intitle:" + URLEncoder.encode(title, StandardCharsets.UTF_8)
                         + "+inauthor:" + URLEncoder.encode(author, StandardCharsets.UTF_8);
            String url = "https://www.googleapis.com/books/v1/volumes?q=" + query;
            
            if (!apiKey.isBlank()) {
                url += "&key=" + apiKey;
            }

            GoogleBooksResponse response = restTemplate.getForObject(url, GoogleBooksResponse.class);
            if (response != null && response.getItems() != null && !response.getItems().isEmpty()) {
                return Optional.ofNullable(response.getItems().get(0).getVolumeInfo().getDescription());
            }
        
        } catch (Exception e) {
            System.err.println("Error fetching from Google Books by title: " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Get book description by ISBN.
     */
    @RateLimiter(name = "googleBooksLimiter")
    public Optional<String> fetchDescriptionByIsbn(String isbn) {
        try {
            String query = "isbn:" + URLEncoder.encode(isbn, StandardCharsets.UTF_8);
            String url = "https://www.googleapis.com/books/v1/volumes?q=" + query;
            
            if (!apiKey.isBlank()) {
                url += "&key=" + apiKey;
            }

            GoogleBooksResponse response = restTemplate.getForObject(url, GoogleBooksResponse.class);
            if (response != null && response.getItems() != null && !response.getItems().isEmpty()) {
                return Optional.ofNullable(response.getItems().get(0).getVolumeInfo().getDescription());
            }
        } catch (Exception e) {
            System.err.println("Error fetching from Google Books by ISBN: " + e.getMessage());
        }
        return Optional.empty();
    }
}
