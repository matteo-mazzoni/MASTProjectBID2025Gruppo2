package com.mast.readup.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mast.readup.entities.Utente;
import com.mast.readup.services.UtenteService;

import jakarta.validation.Valid;

@Controller
public class ReadUpMVC {

    // Home + Register
    @GetMapping("/")
    public String home(Model model) {
        if (!model.containsAttribute("Utente")) {
            model.addAttribute("Utente", new Utente());
        }
        return "index";
    }


    // Service injection
    @Autowired
    private UtenteService utenteService;

    // New user registration
    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("Utente") Utente utente,
    BindingResult result, Model model, RedirectAttributes redirectAttributes) {
    
        // Form validation server-side for duplicate nickname and email
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

        // If there are any other errors, redirect to the registration page (home)
        if (result.hasErrors()) {
            return "index";
        }
       
        // If there are no errors, register the user into the database and set the login property to "true"
        utente.setLoggedIn(true);
        utenteService.aggiungiUtente(utente);

        // Messaggio di successo (mostro solo se presente)                           
        redirectAttributes.addFlashAttribute("successMessage",
            "Benvenuto “" + utente.getNickname() + "”! Registrazione avvenuta con successo."
        );

        return "redirect:/";
    }

    // User logout 
   @PostMapping("/logout")
    public String logout(SessionStatus sessionStatus, RedirectAttributes ra, @ModelAttribute("Utente") Utente utente) {
        
        utente.setLoggedIn(false);
        
        sessionStatus.setComplete();
     
        ra.addFlashAttribute("successMessage", "Logout avvenuto con successo.");
        return "redirect:/";
    }
    





    // Profilo
    @GetMapping("/profilo.html")
    public String profilo(Model model) {
        return "profilo";
    }

    // Le mie Booklist
    @GetMapping("/booklist.html")
    public String booklist(Model model) {
        return "booklist";
    }

    // I miei libri
    @GetMapping("/libri.html")
    public String libri(){
        return "book";
    }

    // Sfide
    @GetMapping("/sfide.html")
    public String sfide(Model model) {
        return "sfide";
    }

    // Question and answers
    @GetMapping("/qa.html")
    public String qa() {
        return "qa";
    }
}