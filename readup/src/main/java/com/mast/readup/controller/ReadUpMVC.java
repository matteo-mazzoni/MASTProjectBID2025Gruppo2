package com.mast.readup.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mast.readup.entities.Libro;
import com.mast.readup.entities.Sfida;
import com.mast.readup.entities.Utente;
import com.mast.readup.services.BooklistService;
import com.mast.readup.services.LibreriaService;
import com.mast.readup.services.SfidaService;
import com.mast.readup.services.UtenteService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class ReadUpMVC {

    /* SERVICES INJECTION */

    // Utente Service injection
    @Autowired
    private UtenteService utenteService;
    
    // Booklist Service injection
    @Autowired
    private BooklistService booklistService;

    // Sfida Service injection
    @Autowired
    private SfidaService sfidaService;

    // Libreria Services injection
    @Autowired 
    private LibreriaService libreriaService;

    
    /* CONTROLLERS */
    
    // Home 
    @GetMapping("/")
    public String home(Model model) {
        if (!model.containsAttribute("Utente")) {
            model.addAttribute("Utente", new Utente());
        }
        return "index";
    }

    // New user registration
    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("Utente") Utente utente, BindingResult result,
    Model model, RedirectAttributes redirectAttributes, HttpSession session) {
    
        // Form validation server-side for duplicate nickname and email
        if (!result.hasFieldErrors("nickname") && utenteService.nicknameEsistente(utente.getNickname())) {
              
            result.rejectValue(
                "nickname",
                "error.nickname.duplicate",
                "ATTENZIONE: Questo username è già in uso! RIPROVA."
            );
        }
        
        if (!result.hasFieldErrors("email") && utenteService.emailEsistente(utente.getEmail())) {
             
            result.rejectValue(
                "email",
                "error.email.duplicate",
                "ATTENZIONE: Questa email è già registrata! RIPROVA."
            );
        }

        // If there are any other errors, redirect to the registration page (home)
        if (result.hasErrors()) {
            return "index";
        }
       
        // If there are no errors, register the user into the database and set the login property to "true" in database
        utente.setLoggedIn(true);

        // All the user data are stored in an object Utente-type called saved. The new user is added to the database
        Utente saved = utenteService.aggiungiUtente(utente);

        /* Save the user data in the session.
        This allows to have the exact primary key on later requests— it will be known which record to update, as set loggedIn to false on logout. */
        session.setAttribute("currentUser", saved);

        // Success message (linked to the changed view in HTML)                      
        redirectAttributes.addFlashAttribute("successMessage", "Benvenuto/a " + saved.getNickname() + "!");
        
        // Redirect to the homepage
        return "redirect:/";    
    }

    // User logout
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        
        // select the user from the session
        Utente current = (Utente) session.getAttribute("currentUser");
        
        // if the user is logged in
        if (current != null) {

            // update the property loggedIn in the database to false
            utenteService.cambiaStatusLogin(current.getIdUtente(), false);

            // remove all attributes from the session (including the current user)
            session.invalidate();
        }
    
        // Redirect to the homepage
        return "redirect:/";
    }

    // Le mie Booklist
    @GetMapping("/booklist.html")
    public String booklist(Model model) {
        return "booklist";
    }

    // I miei libri
    @GetMapping("/libri.html")
    public String libri(){
        return "book";
    }

    // Sfide
    @GetMapping("/sfide.html")
    public String listaSfide(Model model, HttpSession session) {
        List<Sfida> sfide = sfidaService.getAll();
        model.addAttribute("sfide", sfide);
        model.addAttribute("currentUserId", getCurrentUserId(session));
        model.addAttribute("sfida", new Sfida()); //creates an empty Sfida object for the form
        return "listaSfide";
    }

    // Question and answers
    @GetMapping("/qa.html")
    public String qa() {
        return "qa";
    }

    private Long getCurrentUserId(HttpSession session) {
        Utente currentUser = (Utente) session.getAttribute("currentUser");
        return (currentUser != null) ? currentUser.getIdUtente() : null;
    }
    
    @GetMapping("/profilo.html")
    public String profilo(Model model, Principal principal) {
        Utente currentUser = null; // Inizializza a null
        List<Libro> libriUtente = new ArrayList<>(); // Inizializza una lista vuota per i libri dell'utente

        if (principal != null && principal.getName() != null) {
            Long userId;
            try {
                userId = Long.valueOf(principal.getName());
                Optional<Utente> utenteOpt = utenteService.findById(userId);
                if (utenteOpt.isPresent()) {
                    currentUser = utenteOpt.get();
                    // RECUPERA I LIBRI DELL'UTENTE QUI
                    libriUtente = libreriaService.getLibriByUtenteId(currentUser.getIdUtente());
                } else {
                    System.err.println("Utente con ID " + userId + " non trovato nel DB per il Principal: " + principal.getName());
                }
            } catch (NumberFormatException e) {
                System.err.println("Errore: Principal.getName() non è un ID numerico valido per la pagina profilo: " + principal.getName());
            }
        } else {
             System.err.println("Principal o Principal.getName() è null nella pagina profilo.");
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("libriUtente", libriUtente); // AGGIUNGI I LIBRI AL MODELLO
        return "profilo";
    }

    @PostMapping("/profile/upload")
    public String handleImageUpload(@RequestParam("image") MultipartFile file, Principal principal) {
        if (principal == null || principal.getName() == null) {
            return "redirect:/login?error=not_authenticated";
        }

        Long userId;
        try {
            userId = Long.valueOf(principal.getName());
        } catch (NumberFormatException e) {
            System.err.println("Errore: Principal.getName() non è un ID numerico valido per l'upload: " + principal.getName());
            return "redirect:/profilo.html?error=invalid_user_id";
        }

        Optional<Utente> utenteOpt = utenteService.findById(userId);
        if (!utenteOpt.isPresent()) {
            return "redirect:/profilo.html?error=user_not_found";
        }

        try {
            utenteService.saveProfileImage(userId, file);
        } catch (IllegalArgumentException e) {
            return "redirect:/profilo.html?error=" + e.getMessage();
        } catch (RuntimeException e) {
            return "redirect:/profilo.html?error=Errore nel salvataggio dell'immagine.";
        }
        return "redirect:/profilo.html";
    }

    // Metodo per recuperare l'immagine (spostato qui da FotoProfiloMVC)
    @GetMapping("/profile/image/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable("id") Long idUtente) {
        byte[] image = utenteService.getProfileImage(idUtente);
        if (image != null) {
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
        } else {
            try {
                ClassPathResource imgFile = new ClassPathResource("static/images/placeholder.png");
                byte[] placeholderImage = StreamUtils.copyToByteArray(imgFile.getInputStream());
                return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(placeholderImage);
            } catch (IOException e) {
                System.err.println("Errore nel caricamento dell'immagine placeholder: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
    }
    
}