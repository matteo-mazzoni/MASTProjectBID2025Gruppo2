package com.mast.readup.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mast.readup.entities.Utente;
import com.mast.readup.services.UtenteService;

import jakarta.validation.Valid;

@Controller
public class ReadUpMVC {

    // Home
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("Utente", new Utente());
        return "index";
    }


    // Service injection
    @Autowired
    private UtenteService utenteService;

    // New user registration
    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("Utente") Utente utente,
    BindingResult result, Model model, RedirectAttributes redirectAttributes) {

        // Stampa dei dati ricevuti dal form
        System.out.println("Nickname: " + utente.getNickname());
        System.out.println("Email: " + utente.getEmail());
        System.out.println("Password: " + utente.getPassword());
        System.out.println("LoggedIn: " + utente.isLoggedIn());

        // //  Checking for duplicate methods 
        // if (utenteService.nicknameEsistente(utente.getNickname())) {
        //     result.rejectValue("nickname", null, "Nickname già in uso");
        // }
        // if (utenteService.emailEsistente(utente.getEmail())) {
        //     result.rejectValue("email", null, "Email già registrata");
        // }

        if (result.hasErrors()) {
            return "index"; 
        }

        utente.setLoggedIn(true);

        utenteService.aggiungiUtente(utente);

        redirectAttributes.addFlashAttribute("successMessage", "La tua registrazione avvenuta con successo, benvenuto" + utente.getNickname() + "!");
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