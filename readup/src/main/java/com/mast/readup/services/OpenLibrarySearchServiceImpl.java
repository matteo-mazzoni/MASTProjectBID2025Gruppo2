package com.mast.readup.services;

import com.mast.readup.dto.OpenLibrarySearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class OpenLibrarySearchServiceImpl implements OpenLibrarySearchService {

    private final RestTemplate restTemplate;
    private final String searchUrl;

    public OpenLibrarySearchServiceImpl(
        RestTemplate restTemplate,
        @Value("${openlibrary.search.url:https://openlibrary.org/search.json?title={title}&author={author}&limit=10}")
        String searchUrl
    ) {
        this.restTemplate = restTemplate;
        this.searchUrl = searchUrl;
    }

    @Override
    public SearchResult search(String title, String author) {
        try {
            String t = URLEncoder.encode(title,  StandardCharsets.UTF_8);
            String a = URLEncoder.encode(author, StandardCharsets.UTF_8);
            var resp = restTemplate.getForObject(searchUrl, OpenLibrarySearchResponse.class, t, a);
            if (resp == null || resp.getDocs() == null) {
                return new SearchResult(Collections.emptyList(), Collections.emptyList());
            }

            List<String> isbns = new ArrayList<>();
            List<String> olids = new ArrayList<>();
            for (var doc : resp.getDocs()) {
                if (doc.getIsbn() != null) isbns.addAll(doc.getIsbn());
                if (doc.getCoverEditionKey() != null) olids.add(doc.getCoverEditionKey());
            }
            return new SearchResult(isbns, olids);
            
        } catch (RestClientException e) {
            System.err.printf("Search API error for '%s' by '%s': %s%n", title, author, e.getMessage());
            return new SearchResult(Collections.emptyList(), Collections.emptyList());
        }
    }
}