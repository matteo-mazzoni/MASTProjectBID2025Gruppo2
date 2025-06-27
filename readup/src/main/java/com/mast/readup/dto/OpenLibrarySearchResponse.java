/**
 * DTO for OpenLibrary Search API response.
 * Uses Lombok annotations for getters, setters, constructors, and toString.
 * Maps the top-level properties of the search response, in particular the array of documents (books).
 * Each Doc contains title, authors and ISBNs.
 * Ignores unknown JSON properties.
 *
 */
package com.mast.readup.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenLibrarySearchResponse {

    /**
     * List of documents returned by the search. Each one represents a book
     */
    private List<Doc> docs;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Doc {

        /** Book title. */
        private String title;

        /** List of author names (JSON field "author_name"). */
        @JsonProperty("author_name")
        private List<String> authorName;

        /** List of ISBN codes. */
        private List<String> isbn;

        /** OpenLibrary cover edition key for fallback. */
        @JsonProperty("cover_edition_key")
        private String coverEditionKey;

        /** Work key ("key"). */
        @JsonProperty("key")
        private String key;
    }
}
