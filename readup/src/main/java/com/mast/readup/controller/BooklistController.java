package com.mast.readup.controller;

import java.util.List;
import java.util.Optional;

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
import com.mast.readup.repos.BooklistRepos;
import com.mast.readup.services.BooklistService;

import jakarta.servlet.http.HttpSession;
@Controller
public class BooklistController {

    @Autowired
    private BooklistService booklistService;

    @Autowired
    private BooklistRepos booklistRepos;


    // Helper: method to get current user ID from session
    private Long getCurrentUserId(HttpSession session) {
        Utente u = (Utente) session.getAttribute("currentUser");
        return (u != null) ? u.getIdUtente() : null;
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
    public String removeBookFromBooklist(@PathVariable("idBooklist") long idBooklist, @PathVariable("idLibro") long idLibro,
    RedirectAttributes redirectAttributes, HttpSession session) {                                                 
                                        
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

}
