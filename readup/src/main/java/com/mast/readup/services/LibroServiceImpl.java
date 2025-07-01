/**
 * Service layer for managing Libro entities (populating missing ISBNs and retrieve the book covers)
 */
package com.mast.readup.services;

import com.mast.readup.entities.Libro;
import com.mast.readup.integration.OpenLibraryClient;
import com.mast.readup.repos.LibroRepos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Implementation of LibroService interface.
 * 
 * Responsibilities:
 * 1. Populate missing ISBNs on startup by calling OpenLibraryClient.
 * 2. Expose methods to retrieve books that already have ISBN.
 * 3. Provide a random subset of books with ISBN for the homepage carousel covers.
 */

@Service
public class LibroServiceImpl implements LibroService {

    // Logger for having messages in the console
    private static final Logger log = LoggerFactory.getLogger(LibroServiceImpl.class);

    @Autowired
    private final LibroRepos libroRepos;
    private final OpenLibraryClient openLibraryClient;

    /* Constructor */
    public LibroServiceImpl(LibroRepos libroRepos, OpenLibraryClient openLibraryClient) {              
        this.libroRepos = libroRepos;
        this.openLibraryClient = openLibraryClient;
    }

    // Retrieves all books from the database
    @Override
    public List<Libro> findAll() {
        return libroRepos.findAll();
    }    

     /**
     * 1) Queries the database for all books missing an ISBN.
     * 2) For each book, invokes OpenLibraryClient to retrieve possible ISBNs.
     * 3) If any ISBN is returned, picks the first one, updates the entity, and saves it.
     */
    @Override
    @Transactional
    public void populateMissingIsbns() {

        // Retrieve all books without ISBN from database
        List<Libro> booksWithoutIsbn = libroRepos.findLibriSenzaIsbn();
        log.info("Trovati {} libri senza ISBN", booksWithoutIsbn.size());

        for (Libro book : booksWithoutIsbn) {
            log.info("→ cercando ISBN per '{}' di '{}'", book.getTitolo(), book.getAutore());
            
            // Call OpenLibraryClient trying to find ISBN
            Optional<List<String>> lookingForIsbn = openLibraryClient.populateHashSetWithAllISBN(
                book.getTitolo(), book.getAutore()
            );

            if (lookingForIsbn.isEmpty()) {
                log.warn("   nessun ISBN trovato per '{}'", book.getTitolo());
                continue;
            }
            List<String> isbns = lookingForIsbn.get();

            // If there is at least one ISBN
            if (!isbns.isEmpty()) {

                // Choose the first in the list and persist
                String chosenIsbn = isbns.get(0);
                book.setIsbn(chosenIsbn);

                // Save the first ISBN found into the database 
                libroRepos.save(book);
                log.info("salvato ISBN '{}' per libro id={}", chosenIsbn, book.getIdLibro());
            }
        }

        log.info("ISBN population completed.");
    }

    /** Returns all books that already have a non-null ISBN. */
    @Override
    public List<Libro> findConIsbn() {
        return libroRepos.findAllByIsbnNotNull();
    }

    /**
     * Retrieves a random subset of books with ISBN.
     * Loads all books with ISBN, shuffles them in memory, and returns the first `count`.
     *
     * @param count number of random books to retrieve
     * @return list of randomly selected books with ISBN
     */
    @Override
    public List<Libro> findRandomCarousel(int count) {
        List<Libro> allIsbnBooks = libroRepos.findAllByIsbnNotNull();
        Collections.shuffle(allIsbnBooks);

        // Return the first `count` books with ISBN shuffled
        return allIsbnBooks.stream().limit(count).collect(Collectors.toList());

    }

    @Override
    public Optional<Libro> findByTitolo(String titolo) {
        // Chiama il metodo corrispondente nel repository per trovare il libro per titolo (case-insensitive)
        return libroRepos.findByTitoloIgnoreCase(titolo);
    }

    @Override
    public List<String> findAllGenres() {
        // chiama il metodo del repository che restituisce i generi distinti
        return libroRepos.findDistinctGenres();
    }


}
