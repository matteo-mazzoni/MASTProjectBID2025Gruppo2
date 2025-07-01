package com.mast.readup.controller;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError; // Import per il debug
import org.springframework.security.crypto.password.PasswordEncoder; // Import per PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mast.readup.entities.Booklist;
import com.mast.readup.entities.Libro;
import com.mast.readup.entities.Utente;
import com.mast.readup.services.BooklistService;
import com.mast.readup.services.LibreriaService;
import com.mast.readup.services.SfidaService;
import com.mast.readup.services.UtenteService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class ProfileController {

    // Service injections
    @Autowired
    private UtenteService utenteService;

    @Autowired
    private LibreriaService libreriaService;

    @Autowired
    private BooklistService booklistService;

    @Autowired
    private SfidaService sfidaService;

    @Autowired
    private PasswordEncoder passwordEncoder; // <--- NUOVA INIEZIONE

    // User profile page
    @GetMapping("/profilo.html")
    public String profilo(Model model, HttpSession session) {

        Utente currentUserActual = (Utente) session.getAttribute("currentUser");
        if (currentUserActual == null) {
            model.addAttribute("errorMessage", "Devi effettuare l'accesso per visualizzare il profilo.");
            return "redirect:/";
        }

        List<Booklist> userBooklists = booklistService.getAllBooklistsByUser(currentUserActual.getNickname());

        model.addAttribute("userBooklists", userBooklists);

        Optional<Utente> utenteFromDbOpt = utenteService.findById(currentUserActual.getIdUtente());
        if (utenteFromDbOpt.isEmpty()) {
            System.err.println("ATTENZIONE: Utente in sessione con ID " + currentUserActual.getIdUtente() + " non trovato nel DB.");
            session.invalidate();
            model.addAttribute("errorMessage", "Sessione utente non valida. Effettua nuovamente il login.");
            return "redirect:/";
        }
        currentUserActual = utenteFromDbOpt.get(); 
        session.setAttribute("currentUser", currentUserActual); 

        Utente currentUserForForm = (Utente) model.getAttribute("currentUserForForm");
        if (currentUserForForm == null) {
            currentUserForForm = currentUserActual;
        }
        model.addAttribute("currentUserForForm", currentUserForForm);

        model.addAttribute("currentUser", currentUserActual); 
        model.addAttribute("userId", currentUserActual.getIdUtente());

        // Se showEditProfileModal è già nel model (es. da un redirect con errori), non sovrascriverlo
        if (!model.containsAttribute("showEditProfileModal")) {
            model.addAttribute("showEditProfileModal", false);
        }

        // --- QUESTE RIGHE SONO CORRETTE E DEVONO RIMANERE ---
        List<Libro> allUserBooks = libreriaService.getLibriByUtenteId(currentUserActual.getIdUtente());
        // Ordina i libri per ID in ordine decrescente (ID più alti = aggiunti più recentemente)
        allUserBooks.sort(Comparator.comparing(Libro::getIdLibro, Comparator.reverseOrder()));
        List<Libro> recentUserBooks = allUserBooks.stream().limit(3).collect(Collectors.toList());
        model.addAttribute("userLibraryBooks", recentUserBooks); // <-- Questa è la lista limitata che vuoi usare

        List<Booklist> allUserBooklists = booklistService.getAllBooklistsByUser(currentUserActual.getNickname());
        // Ordina le booklist per ID in ordine decrescente (ID più alti = create più recentemente)
        allUserBooklists.sort(Comparator.comparing(Booklist::getIdBooklist, Comparator.reverseOrder()));
        // Limita la lista alle prime 3 booklist
        List<Booklist> recentUserBooklists = allUserBooklists.stream().limit(3).collect(Collectors.toList());
        model.addAttribute("userBooklists", recentUserBooklists); // <-- Questa è la lista limitata che vuoi usare

        // --- QUESTE DUE RIGHE SONO QUELLE DA RIMUOVERE O COMMENTARE ---
        // List<Libro> libriUtente = libreriaService.getLibriByUtenteId(currentUserActual.getIdUtente());
        // model.addAttribute("userLibraryBooks", libriUtente); // Rimuovi questa riga

        // model.addAttribute("userBooklists", userBooklists); // Rimuovi questa riga

        // Il conteggio delle booklist deve usare la lista NON limitata per dare il conteggio totale corretto
        // Per ottenere il numero totale delle booklist e sfide devi usare le liste complete
        int numBooklists = booklistService.getAllBooklistsByUser(currentUserActual.getNickname()).size();
        int numChallenges = sfidaService.countSfideByPartecipante(currentUserActual.getIdUtente());

        model.addAttribute("numBooklists", numBooklists);
        model.addAttribute("numChallenges", numChallenges);

        return "profilo";
    }

    // Handle profile image upload
    @PostMapping("/profile/uploadImage")
    public String uploadProfileImage(@RequestParam("profileImage") MultipartFile file, HttpSession session) {
        Utente currentUser = (Utente) session.getAttribute("currentUser");
    
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
    @ResponseBody
    public ResponseEntity<byte[]> getImage(@PathVariable("id") Long idUtente) {
        try {
            // Attempt to retrieve the user's profile image from the service.
            byte[] image = utenteService.getProfileImage(idUtente);
            // Return user's image if found.
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(image);
        } catch (IllegalArgumentException e) {
            // If user not found or no image, load and return a placeholder image.
            try {
                // Path corrected to include "static/" as per project structure.
                ClassPathResource imgFile = new ClassPathResource("static/img/placeholder-profile.jpg");
                byte[] placeholderImage = StreamUtils.copyToByteArray(imgFile.getInputStream());
                return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(placeholderImage);
            } catch (IOException ioException) {
                // Log error if placeholder fails to load.
                System.err.println("Error loading placeholder image: " + ioException.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        } catch (Exception e) {
            // Catch any other unexpected errors during image retrieval.
            System.err.println("Generic error retrieving image: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Endpoint to handle profile update
    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute("currentUserForForm") Utente updatedUser, 
                                BindingResult bindingResult,
                                HttpSession session,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        // Debugging: Stampa gli errori di validazione
        System.out.println("BindingResult has errors (before custom checks): " + bindingResult.hasErrors());
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(error -> {
                if (error instanceof FieldError) {
                    FieldError fieldError = (FieldError) error;
                    System.out.println("Error: " + fieldError.getDefaultMessage() + " Field: " + fieldError.getField() + " Value: " + fieldError.getRejectedValue());
                } else {
                    System.out.println("Global Error: " + error.getDefaultMessage());
                }
            });
        }


        // Retrieve the currently logged-in user from session
        Utente currentUserInSession = (Utente) session.getAttribute("currentUser");
        if (currentUserInSession == null) {
            // If session has expired, redirect to home with error message
            redirectAttributes.addFlashAttribute("errorMessage", "Session expired. Please log in again.");
            return "redirect:/";
        }

        // --- Custom Validations for Duplicate Nickname and Email ---
        // If nickname has changed, check for duplicates
        if (!updatedUser.getNickname().equalsIgnoreCase(currentUserInSession.getNickname())
                && utenteService.nicknameEsistente(updatedUser.getNickname())) {
            bindingResult.rejectValue(
                "nickname",
                "error.nickname.duplicate",
                "ATTENZIONE: Questo nickname è già in uso! Metti un altro nickname."
            );
        }

        // Check if the email has changed AND is already registered
        if (!updatedUser.getEmail().equalsIgnoreCase(currentUserInSession.getEmail())
                && utenteService.emailEsistente(updatedUser.getEmail())) {
            bindingResult.rejectValue(
                "email",
                "error.email.duplicate",
                "ATTENZIONE: Questa email è già registrata con un altro account!"
            );
        }

        // Aggiungi un System.out.println() qui per vedere se updatedUser.getNewPassword() ha un valore
        System.out.println("New Password from form: '" + updatedUser.getNewPassword() + "'");
        System.out.println("Confirm New Password from form: '" + updatedUser.getConfirmNewPassword() + "'");

        

        // Handle Password Change mismatch and add password length validation
        if (updatedUser.getNewPassword() != null && !updatedUser.getNewPassword().isEmpty()) {
            // Validate password confirmation
            if (!updatedUser.getNewPassword().equals(updatedUser.getConfirmNewPassword())) {
                bindingResult.rejectValue(
                    "confirmNewPassword",
                    "error.password.mismatch",
                    "Le nuove password non corrispondono."
                );
                System.out.println("Tentativo di cambiare la password. Nuova password (non criptata): '" + updatedUser.getNewPassword() + "'");
            }
            // Add minimum password length validation for newPassword
            if (updatedUser.getNewPassword().length() < 8) {
                bindingResult.rejectValue(
                    "newPassword",
                    "error.newPassword.size",
                    "La nuova password deve essere lunga almeno 8 caratteri."
                );
            }
        }


        // If there are any validation errors, return to profile page and reopen the modal
        if (bindingResult.hasErrors()) {
            model.addAttribute("currentUserForForm", updatedUser); 
            model.addAttribute("showEditProfileModal", true); 
            return profilo(model, session); 
        }

        try {
            // Retrieve the user from the database to ensure it is a managed entity
            Optional<Utente> utenteFromDbOpt = utenteService.findById(currentUserInSession.getIdUtente());
            if (utenteFromDbOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "User not found for update.");
                return "redirect:/profilo.html";
            }
            Utente utenteToUpdate = utenteFromDbOpt.get();

            // Update fields on the managed entity
            utenteToUpdate.setNickname(updatedUser.getNickname());
            utenteToUpdate.setEmail(updatedUser.getEmail());
            utenteToUpdate.setCitta(updatedUser.getCitta());

            // Apply password change if a new password was provided and passed validation
            if (updatedUser.getNewPassword() != null && !updatedUser.getNewPassword().isEmpty()) {
                String hashedPassword = passwordEncoder.encode(updatedUser.getNewPassword());
                utenteToUpdate.setPassword(hashedPassword);
                System.out.println("Password criptata salvata: " + hashedPassword);
            }
            utenteService.save(utenteToUpdate); // Save the updated user entity to the database

            // Update the session with the new user data
            session.setAttribute("currentUser", utenteToUpdate);
            redirectAttributes.addFlashAttribute("successMessage", "Profilo aggiornato con successo!"); 

        } catch (Exception e) {
            // In case of an unexpected error during update
            redirectAttributes.addFlashAttribute("errorMessage", "Errore durante l'aggiornamento del profilo: " + e.getMessage()); 
        }

        return "redirect:/profilo.html";    // Redirect to the profile page
    }
}