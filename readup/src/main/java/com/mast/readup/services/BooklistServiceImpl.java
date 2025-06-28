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
    }

    @Override
    @Transactional
    public Booklist creaBooklist(Long idUtenteCreatore, String nomeBooklist, String nicknameCreatore) {
        if (nomeBooklist == null || nomeBooklist.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della booklist non può essere vuoto.");
        }

        // Fetch the creator user by ID
        Utente utenteCreatore = utenteRepos.findById(idUtenteCreatore)
                .orElseThrow(() -> new IllegalArgumentException("Utente creatore non trovato con ID: " + idUtenteCreatore));

        // Create and initialize new booklist
        Booklist newBooklist = new Booklist();
        newBooklist.setNome(nomeBooklist);
        newBooklist.setUtenteCreatore(utenteCreatore);
        newBooklist.setListaLibri(new ArrayList<>()); // Initialize empty book list
        
        // Save and return the new booklist
        return booklistRepos.save(newBooklist);
    }

    @Override
    @Transactional
    public void eliminaBooklist(long idBooklist) {
        // Delete booklist by ID
        booklistRepos.deleteById(idBooklist);
    }

    @Override
    public List<Booklist> getAllBooklistsByUser(String nickname) {
        // Find user by nickname
        Utente utente = utenteRepos.findByNickname(nickname)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato con nickname: " + nickname));
        return booklistRepos.findByUtenteCreatore(utente);
    }

    @Override
    @Transactional
    public Booklist addBookToBooklist(long idBooklist, long idLibro, long idUtente) {
        // Find the booklist by ID
        Booklist booklist = booklistRepos.findById(idBooklist)
                .orElseThrow(() -> new IllegalArgumentException("Booklist non trovata con ID: " + idBooklist));
        // Find the book by ID
        Libro libro = libroRepos.findById(idLibro)
                .orElseThrow(() -> new IllegalArgumentException("Libro non trovato con ID: " + idLibro));

        if (booklist.getUtenteCreatore().getIdUtente() != idUtente) {   // Ensure the user is the creator of the booklist
            throw new SecurityException("Non hai i permessi per modificare questa booklist.");
        }

        List<String> libriIdStrings = booklist.getListaLibri(); // Initialize or retrieve the current list of book IDs
        if (libriIdStrings == null) {
            libriIdStrings = new ArrayList<>();
        }

        String libroIdString = String.valueOf(idLibro);
        if (libriIdStrings.contains(libroIdString)) {   // Check if the book is already in the list
            throw new IllegalArgumentException("Il libro è già presente in questa booklist.");
        }

        // Add the book ID to the list and update the booklist
        libriIdStrings.add(libroIdString);
        booklist.setListaLibri(libriIdStrings); 

        return booklistRepos.save(booklist); // Save and return the updated booklist
    }

    @Override
    @Transactional
    public void removeBookFromBooklist(long idBooklist, long idLibro, long idUtente) {
        Booklist booklist = booklistRepos.findById(idBooklist)
                .orElseThrow(() -> new IllegalArgumentException("Booklist non trovata con ID: " + idBooklist));

        if (booklist.getUtenteCreatore().getIdUtente() != idUtente) {   // Ensure the user is the creator of the booklist
            throw new SecurityException("Non hai i permessi per modificare questa booklist.");
        }

        List<String> libriIdStrings = booklist.getListaLibri();
        if (libriIdStrings == null || libriIdStrings.isEmpty()) {   // Check if the list is empty
            throw new IllegalArgumentException("La booklist è già vuota.");
        }

        String libroIdString = String.valueOf(idLibro);
        if (!libriIdStrings.remove(libroIdString)) {    // Attempt to remove the book ID
            throw new IllegalArgumentException("Il libro non è presente in questa booklist.");
        }

        booklist.setListaLibri(libriIdStrings); // Update and save the modified booklist
        booklistRepos.save(booklist); 
    }

    @Override
    public List<Libro> getBooksInBooklist(long idBooklist) {
        // Find the booklist by ID
        Booklist booklist = booklistRepos.findById(idBooklist)
                .orElseThrow(() -> new IllegalArgumentException("Booklist non trovata con ID: " + idBooklist));

        List<String> libriIdStrings = booklist.getListaLibri();
        if (libriIdStrings == null || libriIdStrings.isEmpty()) {   // Return empty list if no books are present
            return new ArrayList<>(); // Ritorna una lista vuota se non ci sono libri
        }

        // Convert string IDs to Long
        List<Long> libriIds = libriIdStrings.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());

        return libroRepos.findAllById(libriIds);     // Fetch and return the list of book entities
    }
}