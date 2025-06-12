package com.mast.readup.services;

import com.mast.readup.entities.Utente;
import com.mast.readup.repos.UtenteRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Necessario per le operazioni di scrittura

import java.util.List;
import java.util.Optional;

@Service // Indica che questa classe è un componente di servizio di Spring
public class UtenteServiceImpl implements UtenteService {

    private final UtenteRepos UtenteRepos;

    @Autowired
    public UtenteServiceImpl(UtenteRepos UtenteRepos) {
        this.UtenteRepos = UtenteRepos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Utente> findAll(Long idUtente) {
        return UtenteRepos.findAll();
    }

    @Override
    public Optional<Utente> findById(Long idUtente) {
        return UtenteRepos.findById(idUtente);
    }
}