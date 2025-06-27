package com.mast.readup.services;

import com.mast.readup.entities.Booklist;
import com.mast.readup.entities.Utente;
import com.mast.readup.repos.BooklistRepos;
import com.mast.readup.repos.UtenteRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Important for transactions

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service // Indicates to Spring that this is a service class
public class BooklistServiceImpl implements BooklistService {

    private final BooklistRepos booklistRepos;
    private final UtenteRepos utenteRepos;

    @Autowired
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
            throw new IllegalArgumentException("The creator user's ID cannot be null.");
        }
        // Retrieve the user from the repository
        Utente utente = utenteRepos.findById(idUtenteCreatore)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + idUtenteCreatore));
        // Retrieve all booklists associated with the user
        return booklistRepos.findByUtenteCreatore(utente);
    }

    @Override
    @Transactional
    public Booklist creaBooklist(Long idUtenteCreatore, String nome, String descrizione){
        // 1. Validate input data
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("The booklist name cannot be empty.");
        }
        if (idUtenteCreatore == null) {
            throw new IllegalArgumentException("The creator user's ID cannot be null.");
        }
        // Retrieve the user from the repository
        Utente utente = utenteRepos.findById(idUtenteCreatore)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + idUtenteCreatore));
        // Creating the booklist
        Booklist booklist = new Booklist();
        booklist.setNome(nome);
        booklist.setDescrizione(descrizione); // Description can be null
        booklist.setUtenteCreatore(utente);
        booklist.setListaLibri(new ArrayList<>()); // Initialize the list as empty

        // 4. Save the Booklist to the database
        return booklistRepos.save(booklist);
    }

    @Override
    public Booklist modificaDatiBooklist(long idBooklist, String nuovoNome, String nuovaDescrizione){
        // 1. Find the booklist
        Optional<Booklist> optionalBooklist = booklistRepos.findById(idBooklist);
        Booklist booklist = optionalBooklist.orElseThrow(() -> new IllegalArgumentException("Booklist not found with ID: " + idBooklist));

        // 2. Modify fields if new values are not null and valid
        if (nuovoNome != null && !nuovoNome.trim().isEmpty()) {
            booklist.setNome(nuovoNome);
        } else if (nuovoNome != null) { // Case where nuovoNome is an empty string or just spaces
            throw new IllegalArgumentException("The booklist name cannot be empty.");
        }

        if (nuovaDescrizione != null) {
            booklist.setDescrizione(nuovaDescrizione);
        }
        // 3. Save the updated booklist
        return booklistRepos.save(booklist);
    }

    @Override
    public Booklist modificaLibriBooklist(long idBooklist, List<String> nuovaListaLibri) {
        // 1. Find the booklist
        Optional<Booklist> optionalBooklist = booklistRepos.findById(idBooklist);
        Booklist booklist = optionalBooklist.orElseThrow(() -> new IllegalArgumentException("Booklist not found with ID: " + idBooklist));

        if (nuovaListaLibri == null) {
            booklist.setListaLibri(new ArrayList<>());
        } else if (nuovaListaLibri.isEmpty()) {
            // Keep the previous list and throw an error
            throw new IllegalArgumentException("The new list of books cannot be empty. The previous list has been kept.");
        } else {
            booklist.setListaLibri(nuovaListaLibri);
        }
        // 3. Save the updated booklist
        return booklistRepos.save(booklist);
    }

    @Override
    public void eliminaBooklist(long idBooklist) {
        booklistRepos.deleteById(idBooklist);
    }

    @Override
    public List<Booklist> getAllBooklistsByUser(String nickname) {
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new IllegalArgumentException("Nickname cannot be null or empty.");
        }
        Utente utente = utenteRepos.findByNickname(nickname)
            .orElseThrow(() -> new IllegalArgumentException("User not found with nickname: " + nickname));
        return booklistRepos.findByUtenteCreatore(utente);
    }

}