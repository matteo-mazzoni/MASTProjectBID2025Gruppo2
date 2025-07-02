package com.mast.readup.repos;

import com.mast.readup.entities.Libreria;
import com.mast.readup.entities.LibreriaKey; // Importa la chiave composita
import com.mast.readup.entities.Libro;
import com.mast.readup.entities.Utente; // Importa l'entità Utente

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibreriaRepos extends JpaRepository<Libreria, LibreriaKey> {
    
    // Find all books associated with a specific user
    List<Libreria> findByUtente(Utente utente);
    
    @Transactional
    void deleteByUtenteAndLibro_IdLibro(Utente utente, Long idLibro);


    // Find a specific book associated with a user
    Optional<Libreria> findByUtenteAndLibro(Utente u, Libro l);
}