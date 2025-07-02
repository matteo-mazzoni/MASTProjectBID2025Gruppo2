package com.mast.readup.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleVolumeInfo {
    @JsonProperty("description")
    private String description;
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}