package com.mast.readup.services;

import com.mast.readup.entities.Booklist;
import com.mast.readup.entities.Libro;
import com.mast.readup.entities.Utente;
import com.mast.readup.repos.BooklistRepos;
import com.mast.readup.repos.LibroRepos;
import com.mast.readup.repos.UtenteRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BooklistServiceImpl implements BooklistService {

    private final BooklistRepos booklistRepos;
    private final UtenteRepos utenteRepos;
    private final LibroRepos libroRepos;

    @Autowired
    public BooklistServiceImpl(BooklistRepos booklistRepos, UtenteRepos utenteRepos, LibroRepos libroRepos) {
        this.booklistRepos = booklistRepos;
        this.utenteRepos = utenteRepos;
        this.libroRepos = libroRepos;
        // Rimuovi l'iniezione di BooklistContieneRepository se non usata altrove
    }

    @Override
    @Transactional
    public Booklist creaBooklist(Long idUtenteCreatore, String nomeBooklist, String nicknameCreatore) {
        if (nomeBooklist == null || nomeBooklist.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della booklist non può essere vuoto.");
        }
        Utente utenteCreatore = utenteRepos.findById(idUtenteCreatore)
                .orElseThrow(() -> new IllegalArgumentException("Utente creatore non trovato con ID: " + idUtenteCreatore));

        Booklist newBooklist = new Booklist();
        newBooklist.setNome(nomeBooklist);
        newBooklist.setUtenteCreatore(utenteCreatore);
        newBooklist.setListaLibri(new ArrayList<>()); // Inizializza la lista vuota
        return booklistRepos.save(newBooklist);
    }

    @Override
    @Transactional
    public void eliminaBooklist(long idBooklist) {
        booklistRepos.deleteById(idBooklist);
    }

    @Override
    public List<Booklist> getAllBooklistsByUser(String nickname) {
        Utente utente = utenteRepos.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato con nickname: " + nickname));
        return booklistRepos.findByUtenteCreatore(utente);
    }

    @Override
    @Transactional
    public Booklist addBookToBooklist(long idBooklist, long idLibro, long idUtente) {
        Booklist booklist = booklistRepos.findById(idBooklist)
                .orElseThrow(() -> new IllegalArgumentException("Booklist non trovata con ID: " + idBooklist));
        Libro libro = libroRepos.findById(idLibro)
                .orElseThrow(() -> new IllegalArgumentException("Libro non trovato con ID: " + idLibro));

        // Verifica che l'utente sia il creatore della booklist
        if (booklist.getUtenteCreatore().getIdUtente() != idUtente) {
            throw new SecurityException("Non hai i permessi per modificare questa booklist.");
        }

        List<String> libriIdStrings = booklist.getListaLibri();
        if (libriIdStrings == null) {
            libriIdStrings = new ArrayList<>();
        }

        String libroIdString = String.valueOf(idLibro);
        if (libriIdStrings.contains(libroIdString)) {
            throw new IllegalArgumentException("Il libro è già presente in questa booklist.");
        }

        libriIdStrings.add(libroIdString);
        booklist.setListaLibri(libriIdStrings); // Aggiorna la lista nella booklist

        return booklistRepos.save(booklist); // Salva la booklist aggiornata
    }

    @Override
    @Transactional
    public void removeBookFromBooklist(long idBooklist, long idLibro, long idUtente) {
        Booklist booklist = booklistRepos.findById(idBooklist)
                .orElseThrow(() -> new IllegalArgumentException("Booklist non trovata con ID: " + idBooklist));

        // Verifica che l'utente sia il creatore della booklist
        if (booklist.getUtenteCreatore().getIdUtente() != idUtente) {
            throw new SecurityException("Non hai i permessi per modificare questa booklist.");
        }

        List<String> libriIdStrings = booklist.getListaLibri();
        if (libriIdStrings == null || libriIdStrings.isEmpty()) {
            throw new IllegalArgumentException("La booklist è già vuota.");
        }

        String libroIdString = String.valueOf(idLibro);
        if (!libriIdStrings.remove(libroIdString)) {
            throw new IllegalArgumentException("Il libro non è presente in questa booklist.");
        }

        booklist.setListaLibri(libriIdStrings); // Aggiorna la lista nella booklist
        booklistRepos.save(booklist); // Salva la booklist aggiornata
    }

    @Override
    public List<Libro> getBooksInBooklist(long idBooklist) {
        Booklist booklist = booklistRepos.findById(idBooklist)
                .orElseThrow(() -> new IllegalArgumentException("Booklist non trovata con ID: " + idBooklist));

        List<String> libriIdStrings = booklist.getListaLibri();
        if (libriIdStrings == null || libriIdStrings.isEmpty()) {
            return new ArrayList<>(); // Ritorna una lista vuota se non ci sono libri
        }

        // Converte le stringhe degli ID in Long
        List<Long> libriIds = libriIdStrings.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());

        // Recupera i libri dal repository
        return libroRepos.findAllById(libriIds);
    }
}