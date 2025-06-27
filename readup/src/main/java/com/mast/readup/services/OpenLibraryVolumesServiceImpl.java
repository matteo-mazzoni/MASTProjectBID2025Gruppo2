package com.mast.readup.services;

import com.mast.readup.dto.OpenLibraryReadVolumesResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OpenLibraryVolumesServiceImpl implements OpenLibraryVolumesService {

    private final RestTemplate restTemplate;
    private final String volumesUrlBase;

    public OpenLibraryVolumesServiceImpl(
            RestTemplate restTemplate,
            @Value("${openlibrary.volumes.url.base:https://openlibrary.org/api/volumes/brief/olid/}")
            String volumesUrlBase) {
        this.restTemplate   = restTemplate;
        this.volumesUrlBase = volumesUrlBase;
    }

    @Override
    public Optional<List<String>> fetchIsbns(String olid) {
        try {
            String encodedOlid = URLEncoder.encode(olid, StandardCharsets.UTF_8);
            String url = volumesUrlBase + encodedOlid + ".json";
            var volResp = restTemplate.getForObject(url, OpenLibraryReadVolumesResponse.class);

            if (volResp != null && volResp.getRecords() != null && !volResp.getRecords().isEmpty()) {
                var record = volResp.getRecords().values().iterator().next();
                var ids = record.getData().getIdentifiers();
                List<String> all = new ArrayList<>();
                if (ids.getIsbn_13() != null) all.addAll(ids.getIsbn_13());
                if (ids.getIsbn_10() != null) all.addAll(ids.getIsbn_10());
                if (!all.isEmpty()) {
                    return Optional.of(all);
                }
            }
        } catch (RestClientException e) {
            System.err.printf("Volumes API error for OLID '%s': %s%n", olid, e.getMessage());
        }
        return Optional.empty();
    }
}