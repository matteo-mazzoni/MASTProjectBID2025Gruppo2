package com.mast.readup.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleBooksResponse {
    @JsonProperty("items")
    private List<GoogleVolume> items;
    public List<GoogleVolume> getItems() { return items; }
    public void setItems(List<GoogleVolume> items) { this.items = items; }
}