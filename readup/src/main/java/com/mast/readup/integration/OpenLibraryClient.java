package com.mast.readup.integration;

import com.mast.readup.services.OpenLibrarySearchService;
import com.mast.readup.services.OpenLibrarySearchService.SearchResult;
import com.mast.readup.services.OpenLibraryVolumesService;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Coordinatore che unisce le due strategie:
 *  1) Search API per titolo+autore → direct ISBNs + OLIDs
 *  2) Volumes API per ciascun OLID → fallback ISBNs
 *
 * Restituisce un unico elenco ordinato e de-duplicato di tutti gli ISBN trovati.
 */
@Component
public class OpenLibraryClient {

    /* Properties */
    private final OpenLibrarySearchService searchService;
    private final OpenLibraryVolumesService volumesService;


    /* Constructor */
    public OpenLibraryClient(
            OpenLibrarySearchService searchService,
            OpenLibraryVolumesService volumesService
    ) {
        this.searchService  = searchService;
        this.volumesService = volumesService;
    }

    /**
     * Punto d’ingresso per la ricerca degli ISBN:
     * 1) chiede al SearchService direct ISBNs + OLIDs
     * 2) per ciascun OLID chiama il VolumesService e recupera altri ISBNs
     *
     * @param title  titolo del libro
     * @param author autore del libro
     * @return Optional con lista di tutti gli ISBN trovati, empty se non ne trova alcuno
     */
    public Optional<List<String>> populateHashSetWithAllISBN(String title, String author) {
        // 1) Call SearchAPI thanks to its service
        SearchResult resultSearchApi = searchService.search(title, author);

        // 2) Initialise a LinkedHashSet to keep ISBN unique and ordered
        Set<String> allIsbns = new LinkedHashSet<>();
        allIsbns.addAll(resultSearchApi.isbns);

        // 3) For every OLID I iterate called VolumesAPI thanks to its service
        for (String olid : resultSearchApi.olids) {

            // If there are ISBNS, add to the LinkedHashSet
            volumesService.fetchIsbns(olid).ifPresent(allIsbns::addAll);     
        }

        // If the LinkedHashSet is empty, return an empty Optional
        if (allIsbns.isEmpty()) {
            return Optional.empty();
        } else {

            // Otherwise, return the LinkedHashSet as a List full of ISBN
            return Optional.of(List.copyOf(allIsbns));
        }
    }
}