package com.mast.readup.services;

import com.mast.readup.entities.Utente;

import java.util.List;
import java.util.Optional;

public interface UtenteService {
    Optional<Utente> findById(Long idUtente);
    List<Utente> findAll(Long idUtente);
}