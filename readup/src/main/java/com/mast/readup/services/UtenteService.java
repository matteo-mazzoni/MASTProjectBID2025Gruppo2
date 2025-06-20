package com.mast.readup.services;

import com.mast.readup.entities.Utente;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

public interface UtenteService {
    Optional<Utente> findById(Long idUtente);
    List<Utente> findAll(Long idUtente);
    Utente aggiungiUtente(Utente utente);
    Utente modificaNickname(long idUtente, String nuovoNickname);
    Utente modificaPassword(long idUtente, String nuovaPassword);
    Utente modificaEmail(long idUtente, String nuovaEmail);
    Utente modificaCitta(long idUtente, String nuovaCitta);
    Utente cambiaStatusLogin(long idUtente, boolean statoLogin);
    void eliminaUtente(long idUtente);
    void saveProfileImage(String name, MultipartFile file);
    byte[] getProfileImage(Long idUtente);
}