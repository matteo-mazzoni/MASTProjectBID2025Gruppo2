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
    public String viewUserBooklists(Model model, HttpSession session) { // MODIFICATO: Rimosso Principal principal
        Long loggedInUserId = getCurrentUserId(session);
        if (loggedInUserId == null) {
            model.addAttribute("errorMessage", "Devi effettuare l'accesso per visualizzare le booklist."); 
            return "redirect:/"; 
        }
        String nickname = ((Utente) session.getAttribute("currentUser")).getNickname();
        List<Booklist> booklists = booklistService.getAllBooklistsByUser(nickname);
        model.addAttribute("booklists", booklists);
        model.addAttribute("newBooklistName", "");
        model.addAttribute("currentUserId", loggedInUserId);
        return "booklist";
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

        if (booklist.getUtenteCreatore().getIdUtente() != currentUserId) {
            model.addAttribute("errorMessage", "Non hai i permessi per visualizzare questa booklist.");
            return "error"; 
        }

        List<Libro> libriInBooklist = booklistService.getBooksInBooklist(idBooklist);
        model.addAttribute("booklist", booklist);
        model.addAttribute("libriInBooklist", libriInBooklist);
        model.addAttribute("currentUserId", currentUserId);
        return "booklist_detail";
    }

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
        try {
            booklistService.addBookToBooklist(idBooklist, idLibro, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Libro aggiunto alla booklist con successo!");
        } catch (SecurityException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Errore: " + e.getMessage());
        }
        return "redirect:/booklist/" + idBooklist;
    }

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
        try {
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

    // Sfide
    @GetMapping("/sfide.html")
    public String listaSfide(Model model, HttpSession session) {
        List<Sfida> sfide = sfidaService.getAll();
        model.addAttribute("sfide", sfide);
        model.addAttribute("currentUserId", getCurrentUserId(session));
        model.addAttribute("sfida", new Sfida()); // Empty Sfida object for the form
        return "sfide";
    }

    @GetMapping("/sfide.html/{id}")
    public String viewChallengeDetails(@PathVariable("id") Long idSfida, Model model, HttpSession session) {
        Long currentUserId = getCurrentUserId(session); 
        
        if (currentUserId == null) { 
            model.addAttribute("errorMessage", "Devi effettuare l'accesso per visualizzare i dettagli della sfida.");
            return "redirect:/";
        }
        
        Utente currentUser = (Utente) session.getAttribute("currentUser");
        
        final String nicknameForLambda = (currentUser != null) ? currentUser.getNickname() : null;
        
        sfidaService.getById(idSfida)
            .ifPresentOrElse(
                sfida -> {
                    model.addAttribute("sfida", sfida);
                    boolean isCreator = nicknameForLambda != null && sfida.getUtenteCreatore() != null && nicknameForLambda.equals(sfida.getUtenteCreatore().getNickname());
                    model.addAttribute("isCreator", isCreator);
                },
                () -> model.addAttribute("errorMessage", "Sfida non trovata.")
            );
        return "challenges/details";
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

    @PostMapping("/{id}/partecipa")
    public String enrollInChallenge(@PathVariable("id") Long idSfida, RedirectAttributes redirectAttributes, HttpSession session) { 
        Long userId = getCurrentUserId(session);
        if (userId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Devi effettuare l'accesso per partecipare alle sfide.");
            return "redirect:/";
        }
        try {
            sfidaService.partecipaSfida(idSfida, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Ti sei iscritto alla sfida con successo!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/sfide"; 
    }


    @PostMapping("/{id}/eliminasfida")
    public String deleteChallenge(@PathVariable("id") Long idSfida, RedirectAttributes redirectAttributes, HttpSession session) { 
        Long userId = getCurrentUserId(session); // Puoi aggiungere un controllo sull'ID utente se solo il creatore può eliminare
        if (userId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Devi effettuare l'accesso per eliminare le sfide.");
            return "redirect:/"; 
        }
        try {
            sfidaService.rimuoviSfida(idSfida);
            redirectAttributes.addFlashAttribute("successMessage", "Sfida eliminata con successo!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/sfide";
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

        Long loggedInUserId = getCurrentUserId(session); 
        Utente currentUser = null;

        if (loggedInUserId != null) {
            Optional<Utente> utenteOpt = utenteService.findById(loggedInUserId);
            if (utenteOpt.isPresent()) {
                currentUser = utenteOpt.get(); 

                session.setAttribute("currentUser", currentUser); 
            } else {
                System.err.println("ATTENZIONE: Utente in sessione con ID " + loggedInUserId + " non trovato nel DB.");
                session.invalidate(); 
                model.addAttribute("errorMessage", "Sessione utente non valida. Effettua nuovamente il login.");
                return "redirect:/"; 
            }
        } else {
            System.out.println("Utente non loggato, reindirizzamento alla homepage o al login.");
            model.addAttribute("errorMessage", "Devi effettuare l'accesso per visualizzare il profilo.");
            return "redirect:/";
        }

        if (!model.containsAttribute("currentUser")) {
            model.addAttribute("currentUser", currentUser);
        }
        
        model.addAttribute("userId", currentUser.getIdUtente()); 
        
        model.addAttribute("currentUser", currentUser);

        // Recupera i libri della libreria
        List<Libro> libriUtente = libreriaService.getLibriByUtenteId(currentUser.getIdUtente());
        model.addAttribute("userLibraryBooks", libriUtente); 

        // Recupera le booklist
        List<Booklist> userBooklists = booklistService.getAllBooklistsByUser(currentUser.getNickname());
        model.addAttribute("userBooklists", userBooklists);

        // Conto delle Booklist e delle sfide
        int numBooklists = userBooklists.size(); 
        int numChallenges = sfidaService.countSfideByPartecipante(currentUser.getIdUtente()); 

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

    // Endpoint per gestire l'aggiornamento delle informazioni del profilo
    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute("currentUser") Utente updatedUser,
                               BindingResult bindingResult,
                               HttpSession session,
                               Model model,
                               RedirectAttributes redirectAttributes) {

        Utente currentUser = (Utente) session.getAttribute("currentUser");
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Sessione scaduta. Effettua nuovamente il login.");
            return "redirect:/";
        }

        // --- Validazioni Personalizzate per Duplicati (Nickname e Email) ---
        if (!updatedUser.getNickname().equalsIgnoreCase(currentUser.getNickname()) && utenteService.nicknameEsistente(updatedUser.getNickname())) {
            bindingResult.rejectValue(
                "nickname",
                "error.nickname.duplicate",
                "ATTENZIONE: Questo username è già in uso! RIPROVA."
            );
        }

        // Verifica email solo se è cambiata e non è quella dell'utente corrente
        if (!updatedUser.getEmail().equalsIgnoreCase(currentUser.getEmail()) && utenteService.emailEsistente(updatedUser.getEmail())) {
            bindingResult.rejectValue(
                "email",
                "error.email.duplicate",
                "ATTENZIONE: Questa email è già registrata! RIPROVA."
            );
        }

        // --- Gestione Errore di Validazione (Jakarta.validation + Duplicati) ---
        if (bindingResult.hasErrors()) {
            model.addAttribute("currentUser", updatedUser); // Passa l'utente con gli errori al modello
            model.addAttribute("showEditProfileModal", true); // Flag per riaprire il modale
            return profilo(model, session); 
        }

        try {
            currentUser.setNickname(updatedUser.getNickname());
            currentUser.setEmail(updatedUser.getEmail());
            currentUser.setCitta(updatedUser.getCitta());

            // GESTIONE PASSWORD 
            if (updatedUser.getNewPassword() != null && !updatedUser.getNewPassword().isEmpty()) {
                if (!updatedUser.getNewPassword().equals(updatedUser.getConfirmNewPassword())) {
                    redirectAttributes.addFlashAttribute("errorMessage", "Le nuove password non corrispondono.");
                    return "redirect:/profilo.html";
                }
                currentUser.setPassword(updatedUser.getNewPassword());
            }

            utenteService.save(currentUser);

            session.setAttribute("currentUser", currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Profilo aggiornato con successo!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Errore durante l'aggiornamento del profilo: " + e.getMessage());
        }

        return "redirect:/profilo.html";
    }
}