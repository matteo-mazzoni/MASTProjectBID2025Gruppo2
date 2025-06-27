package com.mast.readup.services;

import java.util.List;

/**
 * Service responsible for calling the OpenLibrary Search API
 * (/search.json?title={title}&author={author}&limit=10).
 * Returns both direct ISBNs and OLIDs.
 */
public interface OpenLibrarySearchService {
   
    // Return an object type "SearchResult" which contains both direct ISBNs and OLIDs
    SearchResult search(String title, String author);

    class SearchResult {
        public final List<String> isbns;
        public final List<String> olids;
        public SearchResult(List<String> isbns, List<String> olids) {
            this.isbns = isbns;
            this.olids = olids;
        }
    }
}