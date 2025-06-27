/**
 * Populates missing ISBNs for all Libro records by querying OpenLibrary
 * and saving the first ISBN found for each book.
 */
    
package com.mast.readup.services;

import java.util.List;

import com.mast.readup.entities.Libro;


public interface LibroService {

    void populateMissingIsbns();
    List<Libro> findConIsbn();
    List<Libro> findRandomCarousel(int count);

}
