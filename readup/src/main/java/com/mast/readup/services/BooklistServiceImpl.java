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

@Service
public class BooklistServiceImpl implements BooklistService {
    
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

        // Save and return the new booklist
        return booklistRepos.save(newBooklist);
    }

    @Override
    @Transactional
    public void eliminaBooklist(long idBooklist) {
         if (!booklistRepos.existsById(idBooklist)) {
            throw new IllegalArgumentException("Booklist da eliminare non trovata con ID: " + idBooklist);
        }
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

        if (booklist.getLibri().contains(libro)) { // Usa getLibri() direttamente
            throw new IllegalArgumentException("Il libro è già presente in questa booklist.");
        }
        booklist.addLibro(libro);

        return booklistRepos.save(booklist); // Save and return the updated booklist
    }

    @Override
    @Transactional
    public void removeBookFromBooklist(long idBooklist, long idLibro, long idUtente) {
        Booklist booklist = booklistRepos.findById(idBooklist)
                .orElseThrow(() -> new IllegalArgumentException("Booklist non trovata con ID: " + idBooklist));
        Libro libro = libroRepos.findById(idLibro) 
                .orElseThrow(() -> new IllegalArgumentException("Libro non trovato con ID: " + idLibro));

        if (booklist.getUtenteCreatore().getIdUtente() != idUtente) {   // Ensure the user is the creator of the booklist
            throw new SecurityException("Non hai i permessi per modificare questa booklist.");
        }

        if (!booklist.getLibri().contains(libro)) { // Verifica se il libro è effettivamente nella booklist
            throw new IllegalArgumentException("Il libro non è presente in questa booklist.");
        }
        booklist.removeLibro(libro); // Usa il metodo helper definito nell'entità Booklist

        booklistRepos.save(booklist);
    }

    @Override
    public List<Libro> getBooksInBooklist(long idBooklist) {
        // Find the booklist by ID
        Booklist booklist = booklistRepos.findById(idBooklist)
                .orElseThrow(() -> new IllegalArgumentException("Booklist non trovata con ID: " + idBooklist));

        return new ArrayList<>(booklist.getLibri()); // Converti il Set in List se il metodo restituisce List
    }

    @Override
    public Booklist findById(long idBooklist) {
        return booklistRepos.findById(idBooklist)
                .orElseThrow(() -> new IllegalArgumentException("Booklist non trovata con ID: " + idBooklist));
    }
}