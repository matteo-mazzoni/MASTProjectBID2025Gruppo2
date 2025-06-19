package com.mast.readup.services;

import com.mast.readup.entities.Utente;
import com.mast.readup.repos.UtenteRepos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // Indica che questa classe è un componente di servizio di Spring
public class UtenteServiceImpl implements UtenteService {

    @Autowired
    private final UtenteRepos utenteRepos;

    public UtenteServiceImpl(UtenteRepos utenteRepos) {
        this.utenteRepos = utenteRepos;
    }

    @Override
    public List<Utente> findAll(Long idUtente) {
        return utenteRepos.findAll();
    }

    @Override
    public Optional<Utente> findById(Long idUtente) {
        return utenteRepos.findById(idUtente);
    }

    @Override
    public Utente aggiungiUtente(Utente utente) {
        if (utente == null) {
            throw new IllegalArgumentException("L'utente non può essere null.");
        }
        // Controlla se l'utente esiste già
        if (utenteRepos.existsById(utente.getIdUtente())) {
            throw new IllegalArgumentException("L'utente con ID " + utente.getIdUtente() + " esiste già.");
        }
        // Salva l'utente nel repository
        return utenteRepos.save(utente);
    }

    @Override
    public Utente modificaNickname(long idUtente, String nuovoNickname) {
        Optional<Utente> utenteModify = utenteRepos.findById(idUtente);
        if (utenteModify.isPresent()) {
            Utente utente = utenteModify.get();
            utente.setNickname(nuovoNickname);
            return utenteRepos.save(utente);
        } else {
            throw new IllegalArgumentException("Utente con ID " + idUtente + " non trovato.");
        }
    }

    @Override
    public Utente modificaPassword(long idUtente, String nuovaPassword) {
        Optional<Utente> utenteModify = utenteRepos.findById(idUtente);
        if (utenteModify.isPresent()) {
            Utente utente = utenteModify.get();
            utente.setPassword(nuovaPassword);
            return utenteRepos.save(utente);
        } else {
            throw new IllegalArgumentException("Utente con ID " + idUtente + " non trovato.");
        }
    }

    @Override
    public Utente modificaEmail(long idUtente, String nuovaEmail) {
        Optional<Utente> utenteModify = utenteRepos.findById(idUtente);
        if (utenteModify.isPresent()) {
            Utente utente = utenteModify.get();
            utente.setEmail(nuovaEmail);
            return utenteRepos.save(utente);
        } else {
            throw new IllegalArgumentException("Utente con ID " + idUtente + " non trovato.");
        }
    }

    @Override
    public Utente modificaCitta(long idUtente, String nuovaCitta) {
        Optional<Utente> utenteModify = utenteRepos.findById(idUtente);
        if (utenteModify.isPresent()) {
            Utente utente = utenteModify.get();
            utente.setCitta(nuovaCitta);
            return utenteRepos.save(utente);
        } else {
            throw new IllegalArgumentException("Utente con ID " + idUtente + " non trovato.");
        }
    }

    @Override
    public Utente cambiaStatusLogin(long idUtente, boolean statoLogin) {
        Optional<Utente> utenteModify = utenteRepos.findById(idUtente);
        if (utenteModify.isPresent()) {
            Utente utente = utenteModify.get();
            utente.setLoggedIn(statoLogin);
            return utenteRepos.save(utente);
        } else {
            throw new IllegalArgumentException("Utente con ID " + idUtente + " non trovato.");
        }
    }

    @Override
    public void eliminaUtente(long idUtente) {
        Optional<Utente> utente = utenteRepos.findById(idUtente);
        if (utente.isPresent()) {
            utenteRepos.delete(utente.get());
        } else {
            throw new IllegalArgumentException("Utente con ID " + idUtente + " non trovato.");
        }
    }
}