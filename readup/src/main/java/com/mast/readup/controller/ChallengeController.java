package com.mast.readup.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mast.readup.entities.Sfida;
import com.mast.readup.entities.Utente;
import com.mast.readup.services.SfidaService;

import jakarta.servlet.http.HttpSession;


@Controller
public class ChallengeController {

    @Autowired
    private final SfidaService sfidaService;

    public ChallengeController(SfidaService sfidaService) {
        this.sfidaService = sfidaService;
    }

    // Helper method to get current user ID 
    private Long getCurrentUserId(HttpSession session) {
        Utente u = (Utente) session.getAttribute("currentUser");
        return (u != null) ? u.getIdUtente() : null;
    }

    @GetMapping("/sfide")
    public String listaSfide(Model model, HttpSession session) {

        // Fetch all challenges from the database
        List<Sfida> sfide = sfidaService.getAll();  
        
        // List of challenges
        model.addAttribute("sfide", sfide);

        // Currently logged-in user's ID
        model.addAttribute("currentUserId", getCurrentUserId(session)); 
        
        // Empty challenge object for form binding
        model.addAttribute("sfida", new Sfida());   
        
        return "sfide";
    }

    // View details of a specific challenge
    @GetMapping("/sfide.html/{id}")
    public String viewChallengeDetails(@PathVariable("id") Long idSfida, Model model, HttpSession session) {
        Long currentUserId = getCurrentUserId(session); 
        
        // Redirect to login if user is not authenticated
        if (currentUserId == null) {    
            model.addAttribute("errorMessage", "Devi effettuare l'accesso per visualizzare i dettagli della sfida.");
            return "redirect:/";
        }

        // Retrieve current user from session
        Utente currentUser = (Utente) session.getAttribute("currentUser");  
        
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
    public String salvaSfida(@ModelAttribute("sfida") Sfida sfida, @RequestParam("idCreatoreForm") Long idCreatore,
    RedirectAttributes redirectAttributes) {                    
        
        // Add a new challenge using data from the form               
        try {   
            sfidaService.aggiungiSfida(sfida.getNomeSfida(), sfida.getDescrizioneSfida(), sfida.getDataInizio(), sfida.getDataFine(), idCreatore);                        
            redirectAttributes.addFlashAttribute("successMessage", "Sfida creata con successo!");
            return "redirect:/sfide";
        // Handle known validation error
        } catch (IllegalArgumentException e) {  
            redirectAttributes.addFlashAttribute("errorMessage", "Errore nella creazione della sfida: " + e.getMessage());
            return "redirect:/sfide";
        }
    }

    // Enroll current user in a challenge
    @PostMapping("/{id}/partecipa")
    public String enrollInChallenge(@PathVariable("id") Long idSfida, RedirectAttributes redirectAttributes, HttpSession session) { 
        Long userId = getCurrentUserId(session);
        
        // Redirect to login if user is not authenticated
        if (userId == null) {   
            redirectAttributes.addFlashAttribute("errorMessage", "Devi effettuare l'accesso per partecipare alle sfide.");
            return "redirect:/";
        }
        
        // Enroll the user in the selected challenge
        try {   
            sfidaService.partecipaSfida(idSfida, userId);
            redirectAttributes.addFlashAttribute("successMessage", "Ti sei iscritto alla sfida con successo!");
        } catch (RuntimeException e) {  
            // Handle errors such as already participating, challenge not found, etc.
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/sfide"; 
    }

    // Delete a challenge (only available to the creator, ideally)
    @PostMapping("/{id}/eliminasfida")
    public String deleteChallenge(@PathVariable("id") Long idSfida, RedirectAttributes redirectAttributes, HttpSession session) { 
        
        
        Long userId = getCurrentUserId(session); 
        
        // Redirect to login page if user is not authenticated
        if (userId == null) {   
            redirectAttributes.addFlashAttribute("errorMessage", "Devi effettuare l'accesso per eliminare le sfide.");
            return "redirect:/"; 
        }
        

        // Attempt to delete the challenge
        try {   
            sfidaService.rimuoviSfida(idSfida);
            redirectAttributes.addFlashAttribute("successMessage", "Sfida eliminata con successo!");
        
        // Handle any error during deletion
        } catch (RuntimeException e) {  
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/sfide";   
    }

}

