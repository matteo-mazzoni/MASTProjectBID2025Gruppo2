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
    private final UtenteRepos utenteRepos; // Necessario per trovare l'utente

    @Autowired
    public LibreriaServiceImpl(LibreriaRepos libreriaRepos, UtenteRepos utenteRepos) {
        this.libreriaRepos = libreriaRepos;
        this.utenteRepos = utenteRepos;
    }

    @Override
    public List<Libro> getLibriByUtenteId(Long idUtente) {
        if (idUtente == null) {
            throw new IllegalArgumentException("L'ID utente non può essere nullo.");
        }
        
        // Trova l'utente per l'ID. Questo è importante per garantire che l'utente esista.
        Utente utente = utenteRepos.findById(idUtente)
            .orElseThrow(() -> new IllegalArgumentException("Utente con ID " + idUtente + " non trovato."));

        // Recupera tutte le voci della libreria per l'utente specifico
        List<Libreria> libreriaItems = libreriaRepos.findByUtente(utente);

        // Estrai solo gli oggetti Libro dalle voci della libreria
        return libreriaItems.stream()
                            .map(Libreria::getLibro)
                            .collect(Collectors.toList());
    }
}