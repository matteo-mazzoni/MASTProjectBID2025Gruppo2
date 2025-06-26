package com.mast.readup.services;

import com.mast.readup.entities.Libreria;
import com.mast.readup.entities.Libro;
import com.mast.readup.entities.Utente;
import com.mast.readup.repos.LibreriaRepos;
import com.mast.readup.repos.UtenteRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LibreriaServiceImpl implements LibreriaService {

    private final LibreriaRepos libreriaRepos;
    private final UtenteRepos utenteRepos; // Needed to find the user

    @Autowired
    public LibreriaServiceImpl(LibreriaRepos libreriaRepos, UtenteRepos utenteRepos) {
        this.libreriaRepos = libreriaRepos;
        this.utenteRepos = utenteRepos;
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
        return libreriaItems.stream()
                            .map(Libreria::getLibro)
                            .collect(Collectors.toList());
    }
}