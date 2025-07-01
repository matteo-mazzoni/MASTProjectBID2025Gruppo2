/**
 * Populates missing ISBNs for all Libro records by querying OpenLibrary
 * and saving the first ISBN found for each book.
 */
    
package com.mast.readup.services;

import java.util.List;
import java.util.Optional;

import com.mast.readup.entities.Libro;


public interface LibroService {

    void populateMissingIsbns();
    List<Libro> findConIsbn();
    List<Libro> findRandomCarousel(int count);
    List<Libro> findAll();
    List<String> findAllGenres(); 

    Optional<Libro> findByTitolo(String titolo);
}
