package com.mast.readup.services;

import com.mast.readup.entities.Utente;
import com.mast.readup.repos.UtenteRepos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service 
public class UtenteServiceImpl implements UtenteService {

    private final UtenteRepos utenteRepos;

    // Constructor injection for UtenteRepos
    @Autowired
    public UtenteServiceImpl(UtenteRepos utenteRepos) {
        this.utenteRepos = utenteRepos;
    }

    // Retrieves all users (idUtente parameter is unused, consider removing if not needed)
    @Override
    public List<Utente> findAll(Long idUtente) {
        return utenteRepos.findAll();
    }

    // Finds a user by their ID
    @Override
    public Optional<Utente> findById(Long idUtente) {
        return utenteRepos.findById(idUtente);
    }

    // Adds a new user to the database
    @Override
    public Utente aggiungiUtente(Utente utente) {
        if (utente == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        // Check if user already exists by ID (might need to check by nickname/email if ID is auto-generated)
        if (utenteRepos.existsById(utente.getIdUtente())) {
            throw new IllegalArgumentException("User with ID " + utente.getIdUtente() + " already exists.");
        }
        return utenteRepos.save(utente);
    }

    // Modifies user's nickname by ID
    @Override
    public Utente modificaNickname(long idUtente, String nuovoNickname) {
        Optional<Utente> utenteModify = utenteRepos.findById(idUtente);
        if (utenteModify.isPresent()) {
            Utente utente = utenteModify.get();
            utente.setNickname(nuovoNickname);
            return utenteRepos.save(utente);
        } else {
            throw new IllegalArgumentException("User with ID " + idUtente + " not found.");
        }
    }

    // Modifies user's password by ID
    @Override
    public Utente modificaPassword(long idUtente, String nuovaPassword) {
        Optional<Utente> utenteModify = utenteRepos.findById(idUtente);
        if (utenteModify.isPresent()) {
            Utente utente = utenteModify.get();
            utente.setPassword(nuovaPassword);
            return utenteRepos.save(utente);
        } else {
            throw new IllegalArgumentException("User with ID " + idUtente + " not found.");
        }
    }

    // Modifies user's email by ID
    @Override
    public Utente modificaEmail(long idUtente, String nuovaEmail) {
        Optional<Utente> utenteModify = utenteRepos.findById(idUtente);
        if (utenteModify.isPresent()) {
            Utente utente = utenteModify.get();
            utente.setEmail(nuovaEmail);
            return utenteRepos.save(utente);
        } else {
            throw new IllegalArgumentException("User with ID " + idUtente + " not found.");
        }
    }

    // Modifies user's city by ID
    @Override
    public Utente modificaCitta(long idUtente, String nuovaCitta) {
        Optional<Utente> utenteModify = utenteRepos.findById(idUtente);
        if (utenteModify.isPresent()) {
            Utente utente = utenteModify.get();
            utente.setCitta(nuovaCitta);
            return utenteRepos.save(utente);
        } else {
            throw new IllegalArgumentException("User with ID " + idUtente + " not found.");
        }
    }

    // Changes user's login status by ID
    @Override
    public Utente cambiaStatusLogin(long idUtente, boolean statoLogin) {
        Optional<Utente> utenteModify = utenteRepos.findById(idUtente);
        if (utenteModify.isPresent()) {
            Utente utente = utenteModify.get();
            utente.setLoggedIn(statoLogin);
            return utenteRepos.save(utente);
        } else {
            throw new IllegalArgumentException("User with ID " + idUtente + " not found.");
        }
    }

    // Deletes a user by ID
    @Override
    public void eliminaUtente(long idUtente) {
        Optional<Utente> utente = utenteRepos.findById(idUtente);
        if (utente.isPresent()) {
            utenteRepos.delete(utente.get());
        } else {
            throw new IllegalArgumentException("User with ID " + idUtente + " not found.");
        }
    }

    // Saves the profile image for a user
    @Override
    public void saveProfileImage(Long idUtente, MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Image file cannot be empty.");
        }
        if (!file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("File is not a valid image.");
        }
        Optional<Utente> utenteOpt = utenteRepos.findById(idUtente);
        if (!utenteOpt.isPresent()) {
            throw new IllegalArgumentException("User with ID " + idUtente + " not found.");
        }
        Utente utente = utenteOpt.get();
        try {
            utente.setProfileImage(file.getBytes());
            utenteRepos.save(utente);
        } catch (IOException e) {
            throw new RuntimeException("Error saving profile image.", e);
        }
    }

    // Retrieves the profile image for a user by ID
    @Override
    public byte[] getProfileImage(Long idUtente) {
        Optional<Utente> utenteOpt = utenteRepos.findById(idUtente);
        if (!utenteOpt.isPresent()) {
            // Launches exception if user is not found
            throw new IllegalArgumentException("Utente non trovato o non ha un'immagine del profilo.");
        }
        Utente utente = utenteOpt.get();
        byte[] image = utente.getProfileImage();
        if (image == null) {
            // Launches exception if user exists but does not have an image
            throw new IllegalArgumentException("L'utente non ha un'immagine del profilo.");
        }
        return image;
    }

@Override
    public boolean nicknameEsistente(String nickname) {
        return utenteRepos.existsByNickname(nickname);
    }

    // Checks if an email already exists
    @Override
    public boolean emailEsistente(String email) {
        return utenteRepos.existsByEmail(email);
    }
}