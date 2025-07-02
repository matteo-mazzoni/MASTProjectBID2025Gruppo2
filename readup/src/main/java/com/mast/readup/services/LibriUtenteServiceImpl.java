package com.mast.readup.services;

import com.mast.readup.entities.Libreria;
import com.mast.readup.entities.Libro;
import com.mast.readup.entities.Utente;
import com.mast.readup.repos.LibreriaRepos;
import com.mast.readup.repos.LibroRepos;
import com.mast.readup.repos.UtenteRepos;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing books associated with a user.
 * Provides methods to retrieve books by user ID and to add a book to a user's collection.
 */
@Service
public class LibriUtenteServiceImpl implements LibriUtenteService {

    @Autowired
    private final LibreriaRepos libreriaRepos;
    private final UtenteRepos    utenteRepos;
    private final LibroRepos     libroRepos;

    public LibriUtenteServiceImpl(LibreriaRepos libreriaRepos, UtenteRepos utenteRepos,  LibroRepos libroRepos) {
                                          
        this.libreriaRepos = libreriaRepos;
        this.utenteRepos   = utenteRepos;
        this.libroRepos    = libroRepos;
    }

    // Retrieves all books associated with a specific user ID
    @Override
    public List<Libro> getLibriByUtenteId(Long idUtente) {
        if (idUtente == null) {
            throw new IllegalArgumentException("User ID cannot be null.");
        }
        
        // Find the user by ID; crucial to ensure the user exists.
        Utente utente = utenteRepos.findById(idUtente)
            .orElseThrow(() -> new IllegalArgumentException("User with ID " + idUtente + " not found."));

        // Retrieve all library entries for the specific user
        List<Libreria> libreriaItems = libreriaRepos.findByUtente(utente);

        // Extract only the Libro objects from the library entries
        return libreriaItems.stream().map(Libreria::getLibro).collect(Collectors.toList());
                          

    }

    @Override
    public List<Libro> getLibriByUsername(String username) {
        Utente u = utenteRepos.findByNickname(username)
            .orElseThrow(() -> new IllegalArgumentException("Utente '" + username + "' non trovato"));
        return getLibriByUtenteId(u.getIdUtente());
    }


    @Transactional
    @Override
    public Libro addBookToUser(String username, Long bookId) {
        Utente utente = utenteRepos.findByNickname(username).orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
            
        Libro libro = libroRepos.findById(bookId) .orElseThrow(() -> new IllegalArgumentException("Libro non trovato"));
        // Save the association in the Libreria repository (User - Book)
        Libreria entry = new Libreria(libro, utente, "ADDED"); 
        libreriaRepos.save(entry);
        return libro;
    }


    @Transactional
    @Override
    public void removeBookFromUser(String username, Long idLibro) {
    Utente u = utenteRepos.findByNickname(username)
        .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

    libreriaRepos.deleteByUtenteAndLibro_IdLibro(u, idLibro);
}

    @Transactional
    @Override
    public void updateLetto(Long idLibro, boolean letto) {
        // 1) recupera il Libro
        Libro libro = libroRepos.findById(idLibro)
            .orElseThrow(() -> new IllegalArgumentException("Libro con id " + idLibro + " non trovato"));

        // 2) setta il flag
        libro.setLetto(letto);

        // 3) salva
        libroRepos.save(libro);
    }


}