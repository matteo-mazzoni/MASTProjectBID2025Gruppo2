package com.mast.readup.services;

import com.mast.readup.entities.Booklist;
import com.mast.readup.entities.Utente;
import com.mast.readup.repos.BooklistRepos;
import com.mast.readup.repos.UtenteRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importante per le transazioni

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service // Indica a Spring che questa è una classe di servizio
public class BooklistServiceImpl implements BooklistService {

    @Autowired // Inietta il repository tramite il costruttore
    private final BooklistRepos booklistRepos;

    @Autowired // Inietta il repository tramite il costruttore
    private final UtenteRepos utenteRepos;

    public BooklistServiceImpl(UtenteRepos utenteRepos, BooklistRepos booklistRepos) {
        this.booklistRepos = booklistRepos;
        this.utenteRepos = utenteRepos;
    }

    @Override
    public List<Booklist> getAll() {
        return booklistRepos.findAll();
    }

    @Override
    public Optional<Booklist> getById(Long idBooklist) {
        return booklistRepos.findById(idBooklist);
    }

    @Override
    public List<Booklist> getByIdUtenteCreatore(Long idUtenteCreatore) {
    if (idUtenteCreatore == null) {
        throw new IllegalArgumentException("L'ID dell'utente creatore non può essere null.");
        }
    // Recupera l'utente dal repository
    Utente utente = utenteRepos.findById(idUtenteCreatore)
            .orElseThrow(() -> new IllegalArgumentException("Utente non trovato con ID: " + idUtenteCreatore));
    // Recupera tutte le booklist associate all'utente
    return booklistRepos.findByUtenteCreatore(utente);
}

    @Override
    @Transactional
    public Booklist creaBooklist(Long idUtenteCreatore, String nome, String descrizione){
        // 1. Valida i dati in ingresso
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della booklist non può essere vuoto.");
        }
        if (idUtenteCreatore == null) {
            throw new IllegalArgumentException("L'ID dell'utente creatore non può essere null.");
        }
        // Recupera l'utente dal repository
        Utente utente = utenteRepos.findById(idUtenteCreatore)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato con ID: " + idUtenteCreatore));
        // Creating the booklist
        Booklist booklist = new Booklist();
        booklist.setNome(nome);
        booklist.setDescrizione(descrizione); // La descrizione può essere null
        booklist.setUtenteCreatore(utente);
        booklist.setListaLibri(new ArrayList<>()); // Inizializza la lista vuota

        // 4. Salva la Booklist nel database
        return booklistRepos.save(booklist);
    }

    @Override
    public Booklist modificaDatiBooklist(long idBooklist, String nuovoNome, String nuovaDescrizione){
        // 1. Cerca la booklist
        Optional<Booklist> optionalBooklist = booklistRepos.findById(idBooklist);
        Booklist booklist = optionalBooklist.orElseThrow(() -> new IllegalArgumentException("Booklist non trovata con ID: " + idBooklist));

        // 2. Modifica i campi se i nuovi valori non sono nulli e validi
        if (nuovoNome != null && !nuovoNome.trim().isEmpty()) {
            booklist.setNome(nuovoNome);
        } else if (nuovoNome != null) { // Caso in cui nuovoNome è una stringa vuota o solo spazi
            throw new IllegalArgumentException("Il nome della booklist non può essere vuoto.");
        }

        if (nuovaDescrizione != null) {
             booklist.setDescrizione(nuovaDescrizione);
        }
        // 3. Salva la booklist aggiornata
        return booklistRepos.save(booklist);
    }

    @Override
    public Booklist modificaLibriBooklist(long idBooklist, List<String> nuovaListaLibri) {
        // 1. Cerca la booklist
        Optional<Booklist> optionalBooklist = booklistRepos.findById(idBooklist);
        Booklist booklist = optionalBooklist.orElseThrow(() -> new IllegalArgumentException("Booklist non trovata con ID: " + idBooklist));

        if (nuovaListaLibri == null) {
            booklist.setListaLibri(new ArrayList<>());
        } else if (nuovaListaLibri.isEmpty()) {
        // Mantieni la lista precedente e lancia un errore
        throw new IllegalArgumentException("La nuova lista di libri non può essere vuota. La lista precedente è stata mantenuta.");
        } else {
            booklist.setListaLibri(nuovaListaLibri);
        }
        // 3. Salva la booklist aggiornata
        return booklistRepos.save(booklist);
    }

    @Override
    public void eliminaBooklist(long idBooklist) {
        booklistRepos.deleteById(idBooklist);
    }

    
}