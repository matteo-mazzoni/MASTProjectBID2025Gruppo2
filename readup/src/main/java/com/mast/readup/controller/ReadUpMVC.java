package com.mast.readup.controller;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mast.readup.entities.Libro;
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

    // Service injections
    @Autowired
    private UtenteService utenteService;
    
    @Autowired
    private BooklistService booklistService;

    @Autowired
    private SfidaService sfidaService;

    // Libro Service injection
    @Autowired
    private LibroService libroService;


    /* CONTROLLERS */

    // Home
    @GetMapping("/")
    public String home(Model model) {
        if (!model.containsAttribute("Utente")) {
            model.addAttribute("Utente", new Utente());
        }
        List<Libro> carousel = libroService.findRandomCarousel(4);  
        model.addAttribute("libri", carousel);
        return "index";
    }

    // New user registration
    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("Utente") Utente utente, BindingResult result,
    Model model, RedirectAttributes redirectAttributes, HttpSession session) {
    
        // Server-side validation for duplicate nickname and email
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

        // If validation errors exist, return to registration page
        if (result.hasErrors()) {
            return "index";
        }

        // If there are no errors, register the user into the database and set the login property to "true" in database
        utente.setLoggedIn(true);
        Utente saved = utenteService.aggiungiUtente(utente);

        // Store user data in session
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
            utenteService.cambiaStatusLogin(current.getIdUtente(), false);
            session.invalidate(); // Invalidate session
        }

        // Redirect to the homepage
        return "redirect:/";
    }

    // Booklist page
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
        model.addAttribute("sfida", new Sfida()); // Empty Sfida object for the form
        return "listaSfide";
    }

    // Save a new challenge
    @PostMapping("/salvasfida")
    public String salvaSfida(@ModelAttribute("sfida") Sfida sfida,
                             @RequestParam("idCreatoreForm") Long idCreatore,
                             RedirectAttributes redirectAttributes) {
        try {
            sfidaService.aggiungiSfida(sfida.getNomeSfida(), sfida.getDescrizioneSfida(),
                                       sfida.getDataInizio(), sfida.getDataFine(), idCreatore);
            redirectAttributes.addFlashAttribute("successMessage", "Sfida creata con successo!");
            return "redirect:/sfide.html";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Errore nella creazione della sfida: " + e.getMessage());
            return "redirect:/sfide.html";
        }
    }

    // Q&A page
    @GetMapping("/qa.html")
    public String qa() {
        return "qa";
    }

    // Helper method to get current user ID from session
    private Long getCurrentUserId(HttpSession session) {
        Utente currentUser = (Utente) session.getAttribute("currentUser");
        return (currentUser != null) ? currentUser.getIdUtente() : null;
    }
    
    // User profile page
    @GetMapping("/profilo.html")
    public String profilo(Model model, HttpSession session) {

        Utente currentUser = (Utente) session.getAttribute("currentUser");
        List<Libro> libriUtente = new ArrayList<>(); 

        if (currentUser != null) {
            Optional<Utente> utenteOpt = utenteService.findById(currentUser.getIdUtente());
            if (utenteOpt.isPresent()) {
                currentUser = utenteOpt.get(); // Refresh current user data from DB
                libriUtente = libreriaService.getLibriByUtenteId(currentUser.getIdUtente()); // Retrieve user's books
            } else {
                System.err.println("ATTENZIONE: Utente in sessione con ID " + currentUser.getIdUtente() + " non trovato nel DB.");
                session.invalidate(); // Invalidate session if user not found in DB
                model.addAttribute("errorMessage", "Sessione utente non valida. Effettua nuovamente il login.");
                return "redirect:/";
            }
        } else {
            System.out.println("Utente non loggato, reindirizzamento alla homepage o al login.");
            model.addAttribute("errorMessage", "Devi effettuare l'accesso per visualizzare il profilo.");
            return "redirect:/";
        }
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("libriUtente", libriUtente);
        return "profilo";
    }

    // Handle profile image upload
    @PostMapping("/profile/upload")
    public String handleImageUpload(@RequestParam("image") MultipartFile file, HttpSession session) {
        Utente currentUser = (Utente) session.getAttribute("currentUser");
    
        // Explicitly get userId as Long to prevent compilation issues with 'long' vs 'null'
        Long userId = (currentUser != null) ? currentUser.getIdUtente() : null;

        if (currentUser == null || userId == null) {
            return "redirect:/login?error=not_authenticated"; // Redirect if user not in session or ID is null
        }

        try {
            utenteService.saveProfileImage(currentUser.getIdUtente(), file);
        } catch (IllegalArgumentException e) {
            return "redirect:/profilo.html?error=" + e.getMessage();
        } catch (RuntimeException e) {
            return "redirect:/profilo.html?error=Errore nel salvataggio dell'immagine.";
        }
        return "redirect:/profilo.html";
    }

    // Get profile image by user ID
    @GetMapping("/profile/image/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable("id") Long idUtente) {
        byte[] image = utenteService.getProfileImage(idUtente); // Call the service to get the image
        if (image != null) {
            // If the image exists in the DB, return it
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
        } else {
            // Otherwise, load and return the placeholder image
            try {
                ClassPathResource imgFile = new ClassPathResource("resurces/static/imag/placeholder-profile.png");
                byte[] placeholderImage = StreamUtils.copyToByteArray(imgFile.getInputStream());
                return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(placeholderImage);
            } catch (IOException e) {
                System.err.println("Error loading placeholder image: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
    }
}