package com.mast.readup.repos;

import com.mast.readup.entities.Libreria;
import com.mast.readup.entities.LibreriaKey; // Importa la chiave composita
import com.mast.readup.entities.Utente; // Importa l'entità Utente
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibreriaRepos extends JpaRepository<Libreria, LibreriaKey> {
    // Metodo per trovare tutte le voci della libreria associate a un utente specifico
    List<Libreria> findByUtente(Utente utente);
}