package com.mast.readup.controller;

import java.io.IOException;
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

    @Autowired
    private UtenteService utenteService;

    @Autowired
    private LibreriaService libreriaService;

    @Autowired
    private BooklistService booklistService;

    @Autowired
    private SfidaService sfidaService;

    // User profile page
    @GetMapping("/profilo.html")
    public String profilo(Model model, HttpSession session) {

        Utente currentUserActual = (Utente) session.getAttribute("currentUser");
        if (currentUserActual == null) {
            model.addAttribute("errorMessage", "Devi effettuare l'accesso per visualizzare il profilo.");
            return "redirect:/";
        }

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

        if (!model.containsAttribute("showEditProfileModal")) {
            model.addAttribute("showEditProfileModal", false);
        }

        List<Libro> libriUtente = libreriaService.getLibriByUtenteId(currentUserActual.getIdUtente());
        model.addAttribute("userLibraryBooks", libriUtente);

        List<Booklist> userBooklists = booklistService.getAllBooklistsByUser(currentUserActual.getNickname());
        model.addAttribute("userBooklists", userBooklists);

        int numBooklists = userBooklists.size();
        int numChallenges = sfidaService.countSfideByPartecipante(currentUserActual.getIdUtente());

        model.addAttribute("numBooklists", numBooklists);
        model.addAttribute("numChallenges", numChallenges);

        return "profilo";
    }

    // Handle profile image upload
    @PostMapping("/profile/uploadImage")
    public String uploadProfileImage(@RequestParam("profileImage") MultipartFile file, HttpSession session) {
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
    public String updateProfile(@Valid @ModelAttribute("currentUser") Utente updatedUser,
                                BindingResult bindingResult,
                                HttpSession session,
                                Model model,
                                RedirectAttributes redirectAttributes) {

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
                "ATTENZIONE: Questo nikname è già in uso! Metti un altro nickname" // Era "WARNING: This username is already taken! TRY AGAIN."
            );
        }

        // Check if the email has changed AND is already registered
        if (!updatedUser.getEmail().equalsIgnoreCase(currentUserInSession.getEmail())
                && utenteService.emailEsistente(updatedUser.getEmail())) {
            bindingResult.rejectValue(
                "email",
                "error.email.duplicate",
                "ATTENZIONE: Questa email è già registrata con un altro account!" // Era "WARNING: This email is already registered whit another account!"
            );
        }

        // Handle Password Change mismatch
        if (updatedUser.getNewPassword() != null && !updatedUser.getNewPassword().isEmpty()) {
            // Validate password confirmation
            if (!updatedUser.getNewPassword().equals(updatedUser.getConfirmNewPassword())) {
                    bindingResult.rejectValue(
                    "confirmNewPassword",
                    "error.password.mismatch",
                    "Le nuove password non corrispondono." // Era "New passwords do not match."
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
                utenteToUpdate.setPassword(updatedUser.getNewPassword());
            }

            utenteService.save(utenteToUpdate); // Save the updated user entity to the database

            // Update the session with the new user data
            session.setAttribute("currentUser", utenteToUpdate);
            redirectAttributes.addFlashAttribute("successMessage", "Profilo aggiornato con successo!"); // CAMBIA QUI

        } catch (Exception e) {
            // In case of an unexpected error during update
            redirectAttributes.addFlashAttribute("errorMessage", "Errore durante l'aggiornamento del profilo: " + e.getMessage()); // CAMBIA QUI
        }

        return "redirect:/profilo.html";    // Redirect to the profile page
    }

}
    

