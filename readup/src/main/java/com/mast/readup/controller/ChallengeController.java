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

    @GetMapping("/sfide.html")
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

}

