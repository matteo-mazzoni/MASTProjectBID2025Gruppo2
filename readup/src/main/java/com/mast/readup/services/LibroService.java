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

    Optional<Libro> findById(Long id);
    Libro save(Libro libro);
    void deleteById(Long id);

    // Mantieni questo per corrispondenza esatta se serve altrove
    Optional<Libro> findByTitoloIgnoreCase(String titolo);

    // NUOVO: Metodo per la ricerca "globale" che restituisce una lista
    List<Libro> searchLibriByTitolo(String query);

    // Potresti voler esporre anche la ricerca per autore o combinata
    List<Libro> searchLibriByAutore(String query);
    List<Libro> searchLibriByTitoloOrAutore(String query);
}
