package com.mast.readup.services;

import com.mast.readup.entities.Booklist;
import com.mast.readup.entities.Libro;
import com.mast.readup.entities.Utente;
import com.mast.readup.repos.BooklistRepos;
import com.mast.readup.repos.LibroRepos;
import com.mast.readup.repos.UtenteRepos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BooklistServiceImpl implements BooklistService {
    
    // Inizializza il logger
    private static final Logger log = LoggerFactory.getLogger(BooklistServiceImpl.class);

    @Autowired
    private final BooklistRepos booklistRepos;
    private final UtenteRepos utenteRepos;
    private final LibroRepos libroRepos;

    public BooklistServiceImpl(BooklistRepos booklistRepos, UtenteRepos utenteRepos, LibroRepos libroRepos) {
        this.booklistRepos = booklistRepos;
        this.utenteRepos = utenteRepos;
        this.libroRepos = libroRepos;
    }

    @Override
    @Transactional
    public Booklist creaBooklist(Long idUtenteCreatore, String nomeBooklist, String description, Optional<String> initialBookTitle) {
        if (nomeBooklist == null || nomeBooklist.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della booklist non può essere vuoto.");
        }

        Utente utenteCreatore = utenteRepos.findById(idUtenteCreatore)
                .orElseThrow(() -> new IllegalArgumentException("Utente creatore non trovato con ID: " + idUtenteCreatore));

        Booklist newBooklist = new Booklist();
        newBooklist.setNome(nomeBooklist);
        newBooklist.setDescription(description); // Assicurati di impostare la description
        newBooklist.setUtenteCreatore(utenteCreatore);

        if (initialBookTitle.isPresent() && !initialBookTitle.get().trim().isEmpty()) {
            Libro initialLibro = libroRepos.findByTitoloIgnoreCase(initialBookTitle.get().trim())
                    .orElseThrow(() -> new IllegalArgumentException("Libro con titolo '" + initialBookTitle.get() + "' non trovato."));
            newBooklist.addLibro(initialLibro);
            log.info("Aggiunto libro '{}' alla nuova booklist '{}' durante la creazione.", initialLibro.getTitolo(), nomeBooklist);
        }

        Booklist savedBooklist = booklistRepos.save(newBooklist);
        log.info("Booklist '{}' creata con ID: {}", newBooklist.getNome(), newBooklist.getIdBooklist());
        return savedBooklist;
    }

    @Override
    @Transactional
    public void eliminaBooklist(long idBooklist) {
        if (!booklistRepos.existsById(idBooklist)) {
            throw new IllegalArgumentException("Booklist da eliminare non trovata con ID: " + idBooklist);
        }
        booklistRepos.deleteById(idBooklist);
        log.info("Booklist con ID {} eliminata.", idBooklist);
    }

    @Override
    public List<Booklist> getAllBooklistsByUser(String nickname) {
        Utente utente = utenteRepos.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato con nickname: " + nickname));
        return booklistRepos.findByUtenteCreatore(utente);
    }

    @Override
    @Transactional
    public Booklist addBookToBooklist(long idBooklist, long idLibro, long idUtente) throws IllegalArgumentException, SecurityException {
        Booklist booklist = booklistRepos.findById(idBooklist)
                .orElseThrow(() -> new IllegalArgumentException("Booklist non trovata con ID: " + idBooklist));
        Libro libro = libroRepos.findById(idLibro)
                .orElseThrow(() -> new IllegalArgumentException("Libro non trovato con ID: " + idLibro));

        // Controllo di sicurezza: l'utente loggato deve essere il proprietario della booklist
        if (!booklist.getUtenteCreatore().getIdUtente().equals(idUtente)) { 
            throw new SecurityException("Non hai i permessi per modificare questa booklist.");
        }

        if (booklist.getLibri().contains(libro)) {
            log.warn("Il libro '{}' (ID: {}) è già presente nella booklist '{}' (ID: {}).", libro.getTitolo(), libro.getIdLibro(), booklist.getNome(), booklist.getIdBooklist());
            throw new IllegalArgumentException("Il libro è già presente in questa booklist.");
        }
        booklist.addLibro(libro);

        Booklist updatedBooklist = booklistRepos.save(booklist);
        log.info("Aggiunto libro '{}' (ID: {}) alla booklist '{}' (ID: {}).",
                libro.getTitolo(), libro.getIdLibro(), booklist.getNome(), booklist.getIdBooklist());
        return updatedBooklist;
    }

    @Override
    @Transactional
    public void removeBookFromBooklist(long idBooklist, long idLibro, long idUtente) {
        Booklist booklist = booklistRepos.findById(idBooklist)
                .orElseThrow(() -> new IllegalArgumentException("Booklist non trovata con ID: " + idBooklist));
        Libro libro = libroRepos.findById(idLibro) 
                .orElseThrow(() -> new IllegalArgumentException("Libro non trovato con ID: " + idLibro));

        if (!booklist.getUtenteCreatore().getIdUtente().equals(idUtente)) { 
            throw new SecurityException("Non hai i permessi per modificare questa booklist.");
        }

        if (!booklist.getLibri().contains(libro)) {
            throw new IllegalArgumentException("Il libro non è presente in questa booklist.");
        }
        booklist.removeLibro(libro); 

        booklistRepos.save(booklist);
        log.info("Rimosso libro '{}' (ID: {}) dalla booklist '{}' (ID: {}).",
                libro.getTitolo(), libro.getIdLibro(), booklist.getNome(), booklist.getIdBooklist());
    }

    @Override
    public List<Libro> getBooksInBooklist(long idBooklist) {
        Booklist booklist = booklistRepos.findById(idBooklist)
                .orElseThrow(() -> new IllegalArgumentException("Booklist non trovata con ID: " + idBooklist));

        return new ArrayList<>(booklist.getLibri());
    }

    @Override
    public Booklist findById(long idBooklist) {
        // Questa implementazione ora corrisponde alla nuova definizione dell'interfaccia
        return booklistRepos.findById(idBooklist)
                .orElseThrow(() -> new IllegalArgumentException("Booklist non trovata con ID: " + idBooklist));
    }
}