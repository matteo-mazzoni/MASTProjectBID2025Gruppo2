package com.mast.readup.services;

public interface LibroService {
    /**
     * Populates missing ISBNs for all Libro records by querying OpenLibrary
     * and saving the first ISBN found for each book.
     */
void populateMissingIsbns();
}
