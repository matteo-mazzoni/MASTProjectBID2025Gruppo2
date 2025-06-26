package com.mast.readup.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * DTO per la Read API Volumes di OpenLibrary.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class OpenLibraryReadVolumesResponse {

    /**
     * Mappa di record restituiti dalla Read API.
     * Chiave es. "/books/OL23747519M".
     */
    private Map<String, Record> records;

    /**
     * Singolo record nella mappa 'records'.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Record {
        private RecordData data;
    }

    /**
     * Contiene il nodo 'identifiers'.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class RecordData {
        private Identifiers identifiers;
    }

    /**
     * Contiene gli array di ISBN.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Identifiers {
        private List<String> isbn_10;
        private List<String> isbn_13;
    }
}
