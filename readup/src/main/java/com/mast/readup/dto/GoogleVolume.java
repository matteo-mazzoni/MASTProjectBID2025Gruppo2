package com.mast.readup.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleVolume {
    @JsonProperty("volumeInfo")
    private GoogleVolumeInfo volumeInfo;
    public GoogleVolumeInfo getVolumeInfo() { return volumeInfo; }
    public void setVolumeInfo(GoogleVolumeInfo volumeInfo) { this.volumeInfo = volumeInfo; }
}