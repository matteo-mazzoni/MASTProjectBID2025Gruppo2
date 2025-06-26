package com.mast.readup.integration;

import com.mast.readup.dto.OpenLibrarySearchResponse;
import com.mast.readup.dto.OpenLibraryReadVolumesResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class OpenLibraryClient {

    private final RestTemplate restTemplate;
    private final String searchUrl;

    public OpenLibraryClient(RestTemplate restTemplate,
        @Value("${openlibrary.search.url:https://openlibrary.org/search.json?title={title}&author={author}&limit=10}")
        String searchUrl) {
        this.restTemplate = restTemplate;
        this.searchUrl = searchUrl;
    }

    /**
     * Searches OpenLibrary by title and author first; if no ISBNs found,
     * retries with title only. Returns the first non-empty ISBN list.
     */
    public Optional<List<String>> searchIsbns(String title, String author) {
        Optional<List<String>> result = trySearch(title, author);
        if (result.isPresent()) {
            return result;
        }
        return trySearch(title, null);
    }

    /**
     * Calls OpenLibrary search.json with params and returns
     * the first non-empty ISBN list among up to 10 docs.
     */
    private Optional<List<String>> trySearch(String title, String author) {
        try {
            String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
            String urlPattern;
            Object[] uriVariables;
            if (author != null) {
                String encodedAuthor = URLEncoder.encode(author, StandardCharsets.UTF_8);
                urlPattern = searchUrl;
                uriVariables = new Object[]{encodedTitle, encodedAuthor};
            } else {
                urlPattern = searchUrl.replace("&author={author}", "");
                uriVariables = new Object[]{encodedTitle};
            }

            OpenLibrarySearchResponse response = restTemplate.getForObject(
                urlPattern, OpenLibrarySearchResponse.class, uriVariables);

            if (response != null && response.getDocs() != null) {
                for (OpenLibrarySearchResponse.Doc doc : response.getDocs()) {
                    List<String> isbnList = doc.getIsbn();
                    if (isbnList != null && !isbnList.isEmpty()) {
                        return Optional.of(isbnList);
                    }
                    String coverKey = doc.getCoverEditionKey();
                    if (coverKey != null) {
                        Optional<List<String>> readFallback = fetchIsbnsFromReadApi(coverKey);
                        if (readFallback.isPresent()) {
                            return readFallback;
                        }
                    }
                }
            }
        } catch (RestClientException ex) {
            System.err.printf("Error calling OpenLibrary API for '%s' by '%s': %s%n",
                              title, author, ex.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Fallback: uses Volumes Read API to fetch ISBNs via cover_edition_key as OLID.
     */
    private Optional<List<String>> fetchIsbnsFromReadApi(String coverEditionKey) {
        try {
            String olid = URLEncoder.encode(coverEditionKey, StandardCharsets.UTF_8);
            String url = "https://openlibrary.org/api/volumes/brief/olid/" + olid + ".json";

            OpenLibraryReadVolumesResponse resp = restTemplate.getForObject(url, OpenLibraryReadVolumesResponse.class);
            if (resp != null && resp.getRecords() != null && !resp.getRecords().isEmpty()) {
                Map<String, OpenLibraryReadVolumesResponse.Record> records = resp.getRecords();
                OpenLibraryReadVolumesResponse.Record record = records.values().iterator().next();
                OpenLibraryReadVolumesResponse.Identifiers ids = record.getData().getIdentifiers();
                List<String> allIsbns = new ArrayList<>();
                if (ids.getIsbn_13() != null) allIsbns.addAll(ids.getIsbn_13());
                if (ids.getIsbn_10() != null) allIsbns.addAll(ids.getIsbn_10());
                if (!allIsbns.isEmpty()) {
                    return Optional.of(allIsbns);
                }
            }
        } catch (Exception ex) {
            System.err.printf("Read API error for OLID '%s': %s%n", coverEditionKey, ex.getMessage());
        }
        return Optional.empty();
    }
}
