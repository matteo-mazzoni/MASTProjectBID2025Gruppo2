/**
 * Service layer for managing Libro entities and populating missing ISBNs.
 */
package com.mast.readup.services;

import com.mast.readup.integration.OpenLibraryClient;
import com.mast.readup.entities.Libro;
import com.mast.readup.repos.LibroRepos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LibroServiceImpl implements LibroService {

    private final LibroRepos libroRepos;
    private final OpenLibraryClient openLibraryClient;
    private static final Logger log = LoggerFactory.getLogger(LibroServiceImpl.class);
    

    /**
     * Constructor injection of repository and OpenLibrary client.
     *
     * @param libroRepos repository for Libro entities
     * @param openLibraryClient client to fetch ISBNs from OpenLibrary
     */
    public LibroServiceImpl(LibroRepos libroRepos, OpenLibraryClient openLibraryClient) {
        this.libroRepos = libroRepos;
        this.openLibraryClient = openLibraryClient;
    }

    @Override
    @Transactional
    public void populateMissingIsbns() {
        List<Libro> libri = libroRepos.findLibriSenzaIsbn();
        
        log.info("Trovati {} libri senza ISBN", libri.size());

        for (Libro libro : libri) {
            String titolo = libro.getTitolo();
            String autore = libro.getAutore();
            log.info("→ Chiamata API per titolo='{}', autore='{}'", titolo, autore);

            Optional<List<String>> isbns = openLibraryClient.searchIsbns(titolo, autore);
            
            if (isbns.isEmpty()) {
                log.warn("   Nessun risultato per '{}'", titolo);
                continue;
            }
            
            List<String> list = isbns.get();
            
            log.info("   Risposta API: {}", list);

            if (!list.isEmpty()) {
                String primo = list.get(0);
                libro.setIsbn(primo);
                libroRepos.save(libro);
                log.info("   Salvato ISBN='{}' per libro id={}", primo, libro.getIdLibro());
            }
        }

        log.info("ISBN population completed.");
    }   
}

