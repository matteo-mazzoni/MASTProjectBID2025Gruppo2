package com.mast.readup.controller;

import java.io.IOException;
import java.security.Principal;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.mast.readup.entities.Booklist;
import com.mast.readup.entities.Libro;
import com.mast.readup.entities.Utente;
import com.mast.readup.services.BooklistService;
import com.mast.readup.services.LibreriaService;
import com.mast.readup.services.SfidaService;
import com.mast.readup.services.UtenteService;

import jakarta.servlet.http.HttpSession;

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

    // Helper: method to get current user ID from session
    private Long getCurrentUserId(HttpSession session) {
        Utente u = (Utente) session.getAttribute("currentUser");
        return (u != null) ? u.getIdUtente() : null;
    }

    // User profile page
  @GetMapping("/profilo.html") 
    public String profilo(Model model, HttpSession session,  Principal principal) {

        // Retrieve the logged-in user's ID from the session
        Long loggedInUserId = getCurrentUserId(session); 
        Utente currentUser = null;


        if (loggedInUserId != null) {
            // Attempt to find the user in the database
            Optional<Utente> utenteOpt = utenteService.findById(loggedInUserId);
            if (utenteOpt.isPresent()) {
                currentUser = utenteOpt.get(); 
                
                // Store user info in session for later use
                session.setAttribute("currentUser", currentUser); 
            } else {
                // Handle the case where the session has a user ID not present in the DB
                System.err.println("ATTENZIONE: Utente in sessione con ID " + loggedInUserId + " non trovato nel DB.");
                
                // Invalidate session to prevent unauthorized access
                session.invalidate(); 
                model.addAttribute("errorMessage", "Sessione utente non valida. Effettua nuovamente il login.");
                return "redirect:/"; 
            }
        } else {    
            // No user is logged in; redirect to login/home
            System.out.println("Utente non loggato, reindirizzamento alla homepage o al login.");
            model.addAttribute("errorMessage", "Devi effettuare l'accesso per visualizzare il profilo.");
            return "redirect:/";
        }
        
        // Pass user info to the view
        model.addAttribute("userId", currentUser.getIdUtente()); 
        model.addAttribute("currentUser", currentUser);

        // Fetch user's books from their personal library
        List<Libro> libriUtente = libreriaService.getLibriByUtenteId(currentUser.getIdUtente());
        model.addAttribute("userLibraryBooks", libriUtente); 

        // Fetch all booklists created by the user
        List<Booklist> userBooklists = booklistService.getAllBooklistsByUser(currentUser.getNickname());
        model.addAttribute("userBooklists", userBooklists);

        // Count number of booklists and challenges associated with the user
        int numBooklists = userBooklists.size(); 
        int numChallenges = sfidaService.countSfideByPartecipante(currentUser.getIdUtente()); 

        // Pass counts to the view for display
        model.addAttribute("numBooklists", numBooklists);
        model.addAttribute("numChallenges", numChallenges);

        return "profilo"; 
    }

    // Handle profile image upload
    @PostMapping("/profile/uploadImage")
    public String uploadProfileImage(@RequestParam("profileImage") MultipartFile file, HttpSession session) {
        Utente currentUser = (Utente) session.getAttribute("currentUser");  // Retrieve the user object from the session
    
        Long userId = (currentUser != null) ? currentUser.getIdUtente() : null; // Get user ID safely

        if (currentUser == null || userId == null) {    // Redirect to login if user is not authenticated
            return "redirect:/login?error=not_authenticated";
        }

        try {   // Attempt to save the uploaded profile image
            utenteService.saveProfileImage(currentUser.getIdUtente(), file);
        } catch (IllegalArgumentException e) {  // Handle known validation error from service
            return "redirect:/profilo.html?error=" + e.getMessage();
        } catch (RuntimeException e) {  // Handle any other unexpected error
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
}
    

