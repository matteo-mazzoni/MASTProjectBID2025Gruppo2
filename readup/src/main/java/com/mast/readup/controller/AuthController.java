package com.mast.readup.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mast.readup.entities.Utente;
import com.mast.readup.services.LibroService;
import com.mast.readup.services.UtenteService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class AuthController {

    @Autowired
    private LibroService libroService;

    @Autowired
    private UtenteService utenteService;


   @GetMapping({"/", "/login", "/register"})
    public String index(Model model, @ModelAttribute("successMessage") String successMessage,
    @ModelAttribute("loginError") String loginError, @RequestParam(value="showLogin", required=false) Boolean showLogin) {

        // Carousel
        model.addAttribute("libri", libroService.findRandomCarousel(4));


        if (!model.containsAttribute("utente")) {
            model.addAttribute("utente", new Utente());
        }

        model.addAttribute("successMessage", successMessage);
        model.addAttribute("loginError", loginError);
        model.addAttribute("showLogin", (showLogin != null) && showLogin);
        

        return "index";

    }
    
    /* NEW USER REGISTRATION */

    /**
     * Handles the registration process for a new user.
     * Performs server-side validation to check for duplicate nickname and email.
     * If validation errors exist, returns to the registration page.
     * If there are no errors, registers the user into the database and sets the login property to "true" in database.
     * Stores user data in session and redirects to the homepage with a success message.
     * @param utente The user to be registered
     * @param result The result of the validation
     * @param model The model to store the data in
     * @param redirectAttributes The redirect attributes to store the success message in
     * @param session The session to store the user data in
     * @return The view name to redirect to
     */

    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("Utente") Utente utente,
        BindingResult result, RedirectAttributes redirectAttributes, HttpSession session) {
       
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


    /* USER LOGOUT */
    @PostMapping("/logout")
    public String customLogout(HttpSession session) {
        Utente u = (Utente) session.getAttribute("currentUser");
        if (u != null) {
            u.setLoggedIn(false);
            utenteService.aggiornaUtente(u);   
        }
        session.invalidate();                   
        return "redirect:/?logout=true";       
    }
}


