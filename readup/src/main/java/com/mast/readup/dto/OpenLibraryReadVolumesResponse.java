package com.mast.readup.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * DTO for OpenLibrary Read API Volumes.
 * It contains a Map of records where the Key is a string that starts with "/books/" and ends with "M".
 * 
 * How does it work?
 * 
 * 1. Jackson, the Springbook library for deserializing JSON into Java objects, read the JSON returned by
 * OpenLibrary API and find the node records, a map.
 * It iterates on the map and for each key it deserializes one single Record object.
 * 
 * 2. In Record it finds the field data and populates an instance of RecordData.
 * 
 * 3. In RecordData it finds the field "identifiers" and populates an instance of Identifiers.
 * 
 * 4. In Identifiers it finds the fields "isbn_10" and "isbn_13" and populates the lists.    
 * 
 * 5. Every other JSON field is ignored because of @JsonIgnoreProperties(ignoreUnknown = true).
 * 
 * 6. At the end of the chain, Jackson populates the OpenLibraryReadVolumesResponse object that contains only the ISBNs.
 * They are manipulated via getter methods.
 * 
 **/


 /* Jackson annotation for ignoring unknown JSON properties during deserialization */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class OpenLibraryReadVolumesResponse {

    /**
     * Map Records field returned by OpenLibrary API.
     * Key example: "/books/OL23747519M".
     */
    private Map<String, Record> records;


    /**
     * This class contains a single record of OpenLibrary API.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Record {
        private RecordData data;
    }


    /**
     * This class contains the fields "identifiers" returned by OpenLibrary API that contain ISBNs.
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
