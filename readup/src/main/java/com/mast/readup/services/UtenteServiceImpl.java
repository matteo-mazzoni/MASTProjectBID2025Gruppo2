package com.mast.readup.services;

import com.mast.readup.entities.Utente;
import com.mast.readup.repos.UtenteRepos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service 
public class UtenteServiceImpl implements UtenteService, UserDetailsService {
    
    @Autowired
    private final UtenteRepos utenteRepos;

    // Constructor injection for UtenteRepos
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


    // Methods for checking data during user's registration
    @Override
    public boolean nicknameEsistente(String nickname) {
        return utenteRepos.existsByNickname(nickname);
    }

    @Override
    public boolean emailEsistente(String email) {
        return utenteRepos.existsByEmail(email);
    }


    @Override
    public Optional<Utente> findByNickname(String nickname) {
        return utenteRepos.findByNickname(nickname);
    }

    /**
     * SpringSecurity built-in method
     * Load user data from database by nickname (username).
     * 
     * Authentication flow:
     * 
     *  1. User compile username + password and sends the form to /login.
     *  2. Spring Security calls loadUserByUsername(...) on our service.
     *  3. We compare the hashed password from the form with the password from the database.
     *  4. If they match, we create an authenticated session in which the principal is that UserDetails object.
     */

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        // Find user in database by given nickname or throw exception
        Utente utente = this.findByNickname(username).orElseThrow(() ->
            new UsernameNotFoundException("Utente non trovato con username: " + username)
        );

        // Return user details from database
        // Spring Security uses this Bean object to verify nickname + password when the user try to log in.
        return new org.springframework.security.core.userdetails.User(
            utente.getNickname(),      
            utente.getPassword(),      
            List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}
    
    


