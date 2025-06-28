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
import com.mast.readup.entities.Sfida;
import com.mast.readup.entities.Utente;
import com.mast.readup.repos.BooklistRepos;
import com.mast.readup.services.BooklistService;
import com.mast.readup.services.LibreriaService;
import com.mast.readup.services.LibroService;
import com.mast.readup.services.SfidaService;
import com.mast.readup.services.UtenteService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class ReadUpMVC {

    // Service injections
    @Autowired
    private UtenteService utenteService;

    // Booklist Service injection
    @Autowired
    private BooklistService booklistService;

    // Sfida Service injection
    @Autowired
    private SfidaService sfidaService;

    // Libro Service injection
    @Autowired
    private LibroService libroService;

    // Libreria Service injection
    @Autowired 
    private LibreriaService libreriaService;

    // Booklist Repos injection
    @Autowired 
    private BooklistRepos booklistRepos;

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

        Utente current = (Utente) session.getAttribute("currentUser");   // select the user from the session

        if (current != null) {  // if the user is logged in
            utenteService.cambiaStatusLogin(current.getIdUtente(), false);
            session.invalidate(); // Invalidate session
        }

        return "redirect:/";    // Redirect to the homepage
    }

    // Booklist page – Show user's booklists
    @GetMapping("/booklist.html")
    public String viewUserBooklists(Model model, HttpSession session) { // MODIFICATO: Rimosso Principal principal
        Long loggedInUserId = getCurrentUserId(session);
        if (loggedInUserId == null) {
            model.addAttribute("errorMessage", "Devi effettuare l'accesso per visualizzare le booklist."); 
            return "redirect:/"; 
        }
        
        String nickname = ((Utente) session.getAttribute("currentUser")).getNickname(); // Retrieve nickname from session-stored user object
        List<Booklist> booklists = booklistService.getAllBooklistsByUser(nickname); // Get all booklists created by the user
        model.addAttribute("booklists", booklists); // List of booklists
        model.addAttribute("newBooklistName", ""); // Placeholder for creating a new booklist
        model.addAttribute("currentUserId", loggedInUserId); // ID of the current user
        return "booklist"; // Return the view
    }

    @PostMapping("/salvabooklist")
    public String createBooklist(@RequestParam("name") String name, RedirectAttributes redirectAttributes, HttpSession session) { 
        Long userId = getCurrentUserId(session);
        if (userId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Devi effettuare l'accesso per creare una booklist.");
            return "redirect:/"; 
        }
        try {
            String nickname = ((Utente) session.getAttribute("currentUser")).getNickname();
            booklistService.creaBooklist(userId, name, nickname);
            redirectAttributes.addFlashAttribute("successMessage", "Booklist '" + name + "' creata con successo!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Errore nella creazione della booklist: " + e.getMessage());
        }
        return "redirect:/booklist.html";
    }
    
    // View a specific booklist with its details
    @GetMapping("/booklist/{idBooklist}")
    public String viewBooklistDetails(@PathVariable("idBooklist") long idBooklist, Model model, HttpSession session) {
        Long currentUserId = getCurrentUserId(session);
        if (currentUserId == null) {
            model.addAttribute("errorMessage", "Devi effettuare l'accesso per visualizzare i dettagli della booklist.");
            return "redirect:/"; 
        }

        Optional<Booklist> booklistOpt = booklistRepos.findById(idBooklist);
        if (booklistOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Booklist non trovata.");
            return "error"; 
        }

        Booklist booklist = booklistOpt.get();

        if (booklist.getUtenteCreatore().getIdUtente() != currentUserId) {  // Access control: only the owner can view their booklist
            model.addAttribute("errorMessage", "Non hai i permessi per visualizzare questa booklist.");
            return "error"; 
        }

        List<Libro> libriInBooklist = booklistService.getBooksInBooklist(idBooklist);
        model.addAttribute("booklist", booklist); // Selected booklist
        model.addAttribute("libriInBooklist", libriInBooklist); // Books in the booklist
        model.addAttribute("currentUserId", currentUserId);
        return "booklist_detail"; // View for detailed booklist
    }

    // Add a book to a booklist
    @PostMapping("/booklist/{idBooklist}/add/{idLibro}")
    public String addBookToBooklist(@PathVariable("idBooklist") long idBooklist,
                                    @PathVariable("idLibro") long idLibro,
                                    RedirectAttributes redirectAttributes,
                                    HttpSession session){
        Long userId = getCurrentUserId(session);
        if (userId == null) {
             redirectAttributes.addFlashAttribute("errorMessage", "Devi effettuare l'accesso per aggiungere libri alle booklist.");
             return "redirect:/";
        }

        try {   // Add the book to the user's booklist
            booklistService.addBookToBooklist(idBooklist, idLibro, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Libro aggiunto alla booklist con successo!");
        } catch (SecurityException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Errore: " + e.getMessage());
        }
        return "redirect:/booklist/" + idBooklist;
    }

    // Remove a book from a booklist
    @PostMapping("/booklist/{idBooklist}/remove/{idLibro}")
    public String removeBookFromBooklist(@PathVariable("idBooklist") long idBooklist,
                                         @PathVariable("idLibro") long idLibro,
                                         RedirectAttributes redirectAttributes,
                                         HttpSession session) { 
        Long userId = getCurrentUserId(session);
        if (userId == null) {
             redirectAttributes.addFlashAttribute("errorMessage", "Devi effettuare l'accesso per rimuovere libri dalle booklist.");
             return "redirect:/";
        }

        try {   // Remove the book from the user's booklist
            booklistService.removeBookFromBooklist(idBooklist, idLibro, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Libro rimosso dalla booklist con successo!");
        } catch (SecurityException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Errore: " + e.getMessage());
        }
        return "redirect:/booklist/" + idBooklist;
    }

    // I miei libri
    @GetMapping("/libri.html")
    public String libri(){
        return "book";
    }

    // All challenges
    @GetMapping("/sfide.html")
    public String listaSfide(Model model, HttpSession session) {
        List<Sfida> sfide = sfidaService.getAll();  // Fetch all challenges from the database
        model.addAttribute("sfide", sfide); // List of challenges
        model.addAttribute("currentUserId", getCurrentUserId(session)); // Currently logged-in user's ID
        model.addAttribute("sfida", new Sfida());   // Empty challenge object for form binding
        return "sfide";
    }

    // View details of a specific challenge
    @GetMapping("/sfide.html/{id}")
    public String viewChallengeDetails(@PathVariable("id") Long idSfida, Model model, HttpSession session) {
        Long currentUserId = getCurrentUserId(session); 
        
        if (currentUserId == null) {    // Redirect to login if user is not authenticated
            model.addAttribute("errorMessage", "Devi effettuare l'accesso per visualizzare i dettagli della sfida.");
            return "redirect:/";
        }
        
        Utente currentUser = (Utente) session.getAttribute("currentUser");  // Retrieve current user from session
        
        final String nicknameForLambda = (currentUser != null) ? currentUser.getNickname() : null;
        
        // Retrieve challenge by ID and add details to the model
        sfidaService.getById(idSfida)
            .ifPresentOrElse(
                sfida -> {
                    model.addAttribute("sfida", sfida);
                    // Determine if the current user is the creator of the challenge
                    boolean isCreator = nicknameForLambda != null && sfida.getUtenteCreatore() != null && nicknameForLambda.equals(sfida.getUtenteCreatore().getNickname());
                    model.addAttribute("isCreator", isCreator);
                },
                () -> model.addAttribute("errorMessage", "Sfida non trovata.")
            );
        return "challenges/details";    // Return view with challenge details
    }


    // Save a new challenge submitted via form
    @PostMapping("/salvasfida")
    public String salvaSfida(@ModelAttribute("sfida") Sfida sfida,
                             @RequestParam("idCreatoreForm") Long idCreatore,
                             RedirectAttributes redirectAttributes) {
        try {   // Add a new challenge using data from the form
            sfidaService.aggiungiSfida(sfida.getNomeSfida(), sfida.getDescrizioneSfida(),
                                       sfida.getDataInizio(), sfida.getDataFine(), idCreatore);
            redirectAttributes.addFlashAttribute("successMessage", "Sfida creata con successo!");
            return "redirect:/sfide.html";
        } catch (IllegalArgumentException e) {  // Handle known validation error
            redirectAttributes.addFlashAttribute("errorMessage", "Errore nella creazione della sfida: " + e.getMessage());
            return "redirect:/sfide.html";
        }
    }

    // Enroll current user in a challenge
    @PostMapping("/{id}/partecipa")
    public String enrollInChallenge(@PathVariable("id") Long idSfida, RedirectAttributes redirectAttributes, HttpSession session) { 
        Long userId = getCurrentUserId(session);
        if (userId == null) {   // Redirect to login if user is not authenticated
            redirectAttributes.addFlashAttribute("errorMessage", "Devi effettuare l'accesso per partecipare alle sfide.");
            return "redirect:/";
        }

        try {   // Enroll the user in the selected challenge
            sfidaService.partecipaSfida(idSfida, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Ti sei iscritto alla sfida con successo!");
        } catch (RuntimeException e) {  // Handle errors such as already participating, challenge not found, etc.
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/sfide"; // Redirect back to challenge list
    }

    // Delete a challenge (only available to the creator, ideally)
    @PostMapping("/{id}/eliminasfida")
    public String deleteChallenge(@PathVariable("id") Long idSfida, RedirectAttributes redirectAttributes, HttpSession session) { 
        Long userId = getCurrentUserId(session); // Puoi aggiungere un controllo sull'ID utente se solo il creatore può eliminare
        
        if (userId == null) {   // Redirect to login if user is not authenticated
            redirectAttributes.addFlashAttribute("errorMessage", "Devi effettuare l'accesso per eliminare le sfide.");
            return "redirect:/"; 
        }

        try {   // Attempt to delete the challenge 
            sfidaService.rimuoviSfida(idSfida);
            redirectAttributes.addFlashAttribute("successMessage", "Sfida eliminata con successo!");
        } catch (RuntimeException e) {  // Handle any error during deletion
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/sfide";   // Redirect back to challenge list
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
    public String profilo(Model model, HttpSession session,  Principal principal) {

        // Retrieve the logged-in user's ID from the session
        Long loggedInUserId = getCurrentUserId(session); 
        Utente currentUser = null;

        if (loggedInUserId != null) {
            // Attempt to find the user in the database
            Optional<Utente> utenteOpt = utenteService.findById(loggedInUserId);
            if (utenteOpt.isPresent()) {
                currentUser = utenteOpt.get(); 

                session.setAttribute("currentUser", currentUser); // Store user info in session for later use
            } else {
                // Handle the case where the session has a user ID not present in the DB
                System.err.println("ATTENZIONE: Utente in sessione con ID " + loggedInUserId + " non trovato nel DB.");
                session.invalidate(); // Invalidate session to prevent unauthorized access
                model.addAttribute("errorMessage", "Sessione utente non valida. Effettua nuovamente il login.");
                return "redirect:/"; 
            }
        } else {    // No user is logged in; redirect to login/home
            System.out.println("Utente non loggato, reindirizzamento alla homepage o al login.");
            model.addAttribute("errorMessage", "Devi effettuare l'accesso per visualizzare il profilo.");
            return "redirect:/";
        }
        
        // Pass user info to the view
        model.addAttribute("userId", currentUser.getIdUtente()); 
        model.addAttribute("currentUser", currentUser);

        /// Fetch user's books from their personal library
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

        return "profilo"; // Return the profile view template
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