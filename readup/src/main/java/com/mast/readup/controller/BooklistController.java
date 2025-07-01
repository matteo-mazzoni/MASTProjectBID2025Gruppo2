package com.mast.readup.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mast.readup.entities.Booklist;
import com.mast.readup.entities.Libro;
import com.mast.readup.entities.Utente;
import com.mast.readup.services.BooklistService;
import com.mast.readup.services.LibroService;
import com.mast.readup.services.UtenteService;

@Controller
public class BooklistController {

    @Autowired
    private BooklistService booklistService;

    @Autowired
    private final UtenteService utenteService;

    @Autowired
    private LibroService libroService;

    public BooklistController(BooklistService booklistService, UtenteService utenteService, LibroService libroService) {
        this.booklistService = booklistService;
        this.utenteService = utenteService; 
        this.libroService = libroService;
    }

    // Metodo helper per ottenere l'utente loggato dal Principal
    private Utente getLoggedInUser(Principal principal) {
        if (principal == null) {
            return null;
        }
        return utenteService.findByNickname(principal.getName())
                .orElse(null); // O lancia un'eccezione se l'utente non dovesse mai essere null qui
    }

    // Booklist page – Show user's booklists
    @GetMapping("/booklist.html")
    public String viewUserBooklists(Model model, Principal principal, RedirectAttributes redirectAttributes) { // MODIFICATO: Rimosso Principal principal
        Utente loggedInUser = getLoggedInUser(principal); 
        if (loggedInUser == null) {
            model.addAttribute("errorMessage", "Devi effettuare l'accesso per visualizzare le booklist."); 
            return "redirect:/"; 
        }
        
        List<Booklist> booklists = booklistService.getAllBooklistsByUser(loggedInUser.getNickname()); // Get all booklists created by the user
        model.addAttribute("booklists", booklists); // List of booklists
        model.addAttribute("newBooklistName", ""); // Placeholder for creating a new booklist
        model.addAttribute("currentUserId", loggedInUser); // ID of the current user
        return "booklist"; // Return the view
    }

    @PostMapping("/salvabooklist")
    public String createBooklist(@RequestParam("name") String name, Principal principal, RedirectAttributes redirectAttributes) { 
        Utente loggedInUser = getLoggedInUser(principal);
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Devi effettuare l'accesso per creare una booklist.");
            return "redirect:/"; 
        }
        try {
           booklistService.creaBooklist(loggedInUser.getIdUtente(), name, loggedInUser.getNickname());
           redirectAttributes.addFlashAttribute("successMessage", "Booklist '" + name + "' creata con successo!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Errore nella creazione della booklist: " + e.getMessage());
        }
        return "redirect:/booklist.html";
    }
    
    // View a specific booklist with its details
    @GetMapping("/booklist/{idBooklist}")
    public String viewBooklistDetails(@PathVariable("idBooklist") long idBooklist, 
                                      Principal principal, 
                                      Model model, 
                                      RedirectAttributes redirectAttributes) {
        Utente loggedInUser = getLoggedInUser(principal);
        if (loggedInUser == null) {
            model.addAttribute("errorMessage", "Devi effettuare l'accesso per visualizzare i dettagli della booklist.");
            return "redirect:/"; 
        }

        Booklist booklist = null;
        try {
            booklist = booklistService.findById(idBooklist); // Assicurati che questo metodo esista in BooklistService
        } catch (IllegalArgumentException e) { // Il service dovrebbe lanciare un'eccezione se non trovata
            redirectAttributes.addFlashAttribute("errorMessage", "Booklist non trovata.");
            return "redirect:/booklists.html"; 
        }

        List<Libro> libriInBooklist = booklistService.getBooksInBooklist(idBooklist);
        model.addAttribute("booklist", booklist); // Selected booklist
        model.addAttribute("libriInBooklist", libriInBooklist); // Books in the booklist
        model.addAttribute("currentUserId", loggedInUser);

        return "booklistdettagli"; // View for detailed booklist
    }

    // Add a book to a booklist
    @PostMapping("/booklist/{idBooklist}/add/{idLibro}")
    public String addBookToBooklist(@PathVariable("idBooklist") long idBooklist,
                                    @PathVariable("idLibro") long idLibro,
                                    RedirectAttributes redirectAttributes,
                                    Principal principal){
        Utente loggedInUser = getLoggedInUser(principal);
        if (loggedInUser == null) {
             redirectAttributes.addFlashAttribute("errorMessage", "Devi effettuare l'accesso per aggiungere libri alle booklist.");
             return "redirect:/";
        }

        try {   // Add the book to the user's booklist
            booklistService.addBookToBooklist(idBooklist, idLibro, loggedInUser.getIdUtente());
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
                                         Principal principal) {                                                 
                                        
        Utente loggedInUser = getLoggedInUser(principal);
        if (loggedInUser == null) {
             redirectAttributes.addFlashAttribute("errorMessage", "Devi effettuare l'accesso per rimuovere libri dalle booklist.");
             return "redirect:/";
        }

        try {   // Remove the book from the user's booklist
            booklistService.removeBookFromBooklist(idBooklist, idLibro, loggedInUser.getIdUtente());
            redirectAttributes.addFlashAttribute("successMessage", "Libro rimosso dalla booklist con successo!");
        } catch (SecurityException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Errore: " + e.getMessage());
        }
        return "redirect:/booklist/" + idBooklist;
    }

    @PostMapping("/{idBooklist}/delete") // CAMBIATO: Percorso più pulito
    public String deleteBooklist(@PathVariable("idBooklist") Long idBooklist, Principal principal, RedirectAttributes redirectAttributes) {
        Utente loggedInUser = getLoggedInUser(principal);
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Devi effettuare l'accesso per eliminare le booklist.");
            return "redirect:/";
        }

        try {
            Booklist booklistToDelete = booklistService.findById(idBooklist); // Delega al service
            if (!booklistToDelete.getUtenteCreatore().getIdUtente().equals(loggedInUser.getIdUtente())) { // Confronta ID numerici
                throw new SecurityException("Non hai i permessi per eliminare questa booklist.");
            }
            booklistService.eliminaBooklist(idBooklist);
            redirectAttributes.addFlashAttribute("successMessage", "Booklist eliminata con successo!");
            return "redirect:/booklist.html"; // CAMBIATO: Coerenza con la nuova URL
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/booklist.html"; // O alla pagina di dettaglio se vuoi rimanere lì
        }
    }
}
