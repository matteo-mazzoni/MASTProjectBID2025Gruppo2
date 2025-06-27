package com.mast.readup.services;

import java.util.List;
import java.util.Optional;

/**
 * Service responsible for calling the OpenLibrary Volumes Read API
 * (/api/volumes/brief/olid/{olid}.json).
 * Returns ISBN list given an OLID.
 */
public interface OpenLibraryVolumesService {
    /**
     * Fetch ISBNs by OLID from the Volumes Read API
     * @param olid coverEditionKey (Open Library ID)
     * @return Optional list of ISBNs
     */
    Optional<List<String>> fetchIsbns(String olid);
}