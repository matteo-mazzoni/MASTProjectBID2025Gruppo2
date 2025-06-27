package com.mast.readup.dto;

import java.util.Collections;
import java.util.List;

/**
 * DTO to hold results from the OpenLibrary Search API:
 * - direct ISBNs found in search results
 * - OLIDs (coverEditionKey) for fallback lookups
 */
public class SearchResult {
    private final List<String> isbns;
    private final List<String> olids;

    public SearchResult(List<String> isbns, List<String> olids) {
        this.isbns = isbns != null ? isbns : Collections.emptyList();
        this.olids = olids != null ? olids : Collections.emptyList();
    }

    public List<String> getIsbns() {
        return isbns;
    }

    public List<String> getOlids() {
        return olids;
    }
}
