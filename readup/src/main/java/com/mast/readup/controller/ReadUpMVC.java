package com.mast.readup.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mast.readup.entities.Sfida;
import com.mast.readup.entities.Utente;
import com.mast.readup.services.BooklistService;
import com.mast.readup.services.SfidaService;
import com.mast.readup.services.UtenteService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class ReadUpMVC {

    /* SERVICES INJECTION */

    // Utente Service injection
    @Autowired
    private UtenteService utenteService;
    
    // Booklist Service injection
    @Autowired
    private BooklistService booklistService;

    // Sfida Service injection
    @Autowired
    private SfidaService sfidaService;

    
    /* CONTROLLERS */
    
    // Home 
    @GetMapping("/")
    public String home(Model model) {
        if (!model.containsAttribute("Utente")) {
            model.addAttribute("Utente", new Utente());
        }
        return "index";
    }

    // New user registration
    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("Utente") Utente utente, BindingResult result,
    Model model, RedirectAttributes redirectAttributes, HttpSession session) {
    
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
       
        // If there are no errors, register the user into the database and set the login property to "true" in database
        utente.setLoggedIn(true);

        // All the user data are stored in an object Utente-type called saved. The new user is added to the database
        Utente saved = utenteService.aggiungiUtente(utente);

        /* Save the user data in the session.
        This allows to have the exact primary key on later requests— it will be known which record to update, as set loggedIn to false on logout. */
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

            // update the property loggedIn in the database to false
            utenteService.cambiaStatusLogin(current.getIdUtente(), false);

            // remove all attributes from the session (including the current user)
            session.invalidate();
        }
    
        // Redirect to the homepage
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
    public String listaSfide(Model model, HttpSession session) {
        List<Sfida> sfide = sfidaService.getAll();
        model.addAttribute("sfide", sfide);
        model.addAttribute("currentUserId", getCurrentUserId(session));
        model.addAttribute("sfida", new Sfida()); //creates an empty Sfida object for the form
        return "listaSfide";
    }

    // Question and answers
    @GetMapping("/qa.html")
    public String qa() {
        return "qa";
    }

    private Long getCurrentUserId(HttpSession session) {
        Utente currentUser = (Utente) session.getAttribute("currentUser");
        return (currentUser != null) ? currentUser.getIdUtente() : null;
    }

}