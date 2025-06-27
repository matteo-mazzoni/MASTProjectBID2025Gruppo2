/**
 * Service layer for managing Libro entities (populating missing ISBNs and retrieve the book covers)
 */
package com.mast.readup.services;

import com.mast.readup.entities.Libro;
import com.mast.readup.integration.OpenLibraryClient;
import com.mast.readup.repos.LibroRepos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LibroServiceImpl implements LibroService {

    // Logger for having messages in the console
    private static final Logger log = LoggerFactory.getLogger(LibroServiceImpl.class);

    /* Properties */
    private final LibroRepos libroRepos;
    private final OpenLibraryClient openLibraryClient;

    /* Constructor */
    public LibroServiceImpl(LibroRepos libroRepos, OpenLibraryClient openLibraryClient) {              
        this.libroRepos = libroRepos;
        this.openLibraryClient = openLibraryClient;
    }

    /**
     * 1) Retrieve books without ISBN
     * 2) Per ciascuno chiama OpenLibraryClient per popolare il campo isbn
     * 3) Salva solo il primo ISBN trovato
     */
    @Override
    @Transactional
    public void populateMissingIsbns() {
        List<Libro> senza = libroRepos.findLibriSenzaIsbn();
        log.info("Trovati {} libri senza ISBN", senza.size());

        for (Libro l : senza) {
            log.info("→ cercando ISBN per '{}' di '{}'", l.getTitolo(), l.getAutore());
            Optional<List<String>> maybeIsbns = openLibraryClient.populateHashSetWithAllISBN(
                l.getTitolo(), l.getAutore()
            );
            if (maybeIsbns.isEmpty()) {
                log.warn("   nessun ISBN trovato per '{}'", l.getTitolo());
                continue;
            }
            List<String> isbns = maybeIsbns.get();
            if (!isbns.isEmpty()) {
                String chosen = isbns.get(0);
                l.setIsbn(chosen);
                libroRepos.save(l);
                log.info("   salvato ISBN '{}' per libro id={}", chosen, l.getIdLibro());
            }
        }

        log.info("ISBN population completed.");
    }

    /** Restituisce tutti i libri che hanno un ISBN valorizzato */
    @Override
    public List<Libro> findConIsbn() {
        return libroRepos.findAllByIsbnNotNull();
    }

    /**
     * Restituisce una lista di 'count' libri a caso tra quelli con ISBN
     */
    @Override
    public List<Libro> findRandomCarousel(int count) {
        // usa direttamente la query nativa se count == 4
        if (count == 4) {
            return libroRepos.findRandom4WithIsbn();
        }
        // altrimenti carica tutti, mischia e limita
        List<Libro> all = libroRepos.findAllByIsbnNotNull();
        Collections.shuffle(all);
        return all.stream().limit(count).collect(Collectors.toList());
                 
                  
    }
}
