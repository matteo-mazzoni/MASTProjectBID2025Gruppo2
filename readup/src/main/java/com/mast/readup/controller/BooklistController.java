package com.mast.readup.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mast.readup.entities.Booklist;
import com.mast.readup.entities.Libro;
import com.mast.readup.entities.Utente;
import com.mast.readup.services.BooklistService;
import com.mast.readup.services.LibroService;
import com.mast.readup.services.UtenteService;

@Controller
public class BooklistController {

    private final BooklistService booklistService;
    private final UtenteService utenteService;
    private final LibroService libroService;

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
    public String viewUserBooklists(Model model, Principal principal, RedirectAttributes redirectAttributes) {
        Utente loggedInUser = getLoggedInUser(principal); 
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Devi effettuare l'accesso per visualizzare le booklist."); 
            return "redirect:/"; // Reindirizza alla homepage o pagina di login
        }
        
        List<Booklist> booklists = booklistService.getAllBooklistsByUser(loggedInUser.getNickname());
        model.addAttribute("booklists", booklists);
        model.addAttribute("newBooklistName", new Booklist()); // Placeholder per creare una nuova booklist
        model.addAttribute("currentUserId", loggedInUser.getIdUtente()); // CORRETTO: Passa l'ID per coerenza
        return "booklist";
    }

    @PostMapping("/salvabooklist")
    public String createBooklist(@RequestParam("name") String name,
                                 @RequestParam(value = "description", required = false) String description,
                                 @RequestParam(value = "initialBookTitle", required = false) String initialBookTitle,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        Utente loggedInUser = getLoggedInUser(principal);
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Devi effettuare l'accesso per creare una booklist.");
            return "redirect:/"; 
        }
        try {
            booklistService.creaBooklist(
                loggedInUser.getIdUtente(),
                name,
                description,
                Optional.ofNullable(initialBookTitle).filter(s -> !s.trim().isEmpty()) // Passa il titolo come Optional<String> pulito
            );
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
            redirectAttributes.addFlashAttribute("errorMessage", "Devi effettuare l'accesso per visualizzare i dettagli della booklist.");
            return "redirect:/"; 
        }

        Booklist booklist = null;
        try {
            booklist = booklistService.findById(idBooklist); 
            if (!booklist.getUtenteCreatore().getIdUtente().equals(loggedInUser.getIdUtente())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Non hai i permessi per visualizzare questa booklist.");
                return "redirect:/booklist.html";
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Booklist non trovata: " + e.getMessage());
            return "redirect:/booklist.html"; 
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Errore di accesso: " + e.getMessage());
            return "redirect:/booklist.html";
        }

        List<Libro> libriInBooklist = booklistService.getBooksInBooklist(idBooklist);
        model.addAttribute("booklist", booklist);
        model.addAttribute("libriInBooklist", libriInBooklist);
        model.addAttribute("currentUserId", loggedInUser.getIdUtente()); // Passa l'ID dell'utente loggato

        // AGGIUNTA: per la ricerca di libri nella modal, si assume che esista un metodo searchLibri nel LibroService
        model.addAttribute("allLibri", libroService.findAll()); 

        return "booklistdettagli"; 
    }

    // Add a book to a booklist
    @PostMapping("/booklist/{idBooklist}/add-libro") // CORRETTO: Metodo POST con RequestParam per libroId
    public String addBookToBooklist(@PathVariable("idBooklist") long idBooklist,
                                     @RequestParam("libroId") long libroId, // Accetta l'ID del libro come RequestParam
                                     RedirectAttributes redirectAttributes,
                                     Principal principal){
        Utente loggedInUser = getLoggedInUser(principal);
        if (loggedInUser == null) {
             redirectAttributes.addFlashAttribute("errorMessage", "Devi effettuare l'accesso per aggiungere libri alle booklist.");
             return "redirect:/";
        }

        try { 
            booklistService.addBookToBooklist(idBooklist, libroId, loggedInUser.getIdUtente());
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

        try { 
            booklistService.removeBookFromBooklist(idBooklist, idLibro, loggedInUser.getIdUtente());
            redirectAttributes.addFlashAttribute("successMessage", "Libro rimosso dalla booklist con successo!");
        } catch (SecurityException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Errore: " + e.getMessage());
        }
        return "redirect:/booklist/" + idBooklist;
    }

    @PostMapping("/booklist/{idBooklist}/delete") // CORRETTO: Percorso più pulito e specifico per booklist
    public String deleteBooklist(@PathVariable("idBooklist") Long idBooklist, Principal principal, RedirectAttributes redirectAttributes) {
        Utente loggedInUser = getLoggedInUser(principal);
        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Devi effettuare l'accesso per eliminare le booklist.");
            return "redirect:/";
        }

        try {
            Booklist booklistToDelete = booklistService.findById(idBooklist);
            if (!booklistToDelete.getUtenteCreatore().getIdUtente().equals(loggedInUser.getIdUtente())) {
                throw new SecurityException("Non hai i permessi per eliminare questa booklist.");
            }
            booklistService.eliminaBooklist(idBooklist);
            redirectAttributes.addFlashAttribute("successMessage", "Booklist eliminata con successo!");
            return "redirect:/booklist.html"; 
        } catch (IllegalArgumentException | SecurityException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/booklist.html"; 
        }
    }
    
    // Endpoint per la ricerca di libri via AJAX - RIMOZIONE DI Auth, uso Principal se serve
    @GetMapping("/api/libri/search")
    @ResponseBody 
    public List<Libro> searchLibri(@RequestParam String query) {
        return libroService.findByTitolo(query)
                .map(List::of)
                .orElse(List.of());
    }
}