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


   @GetMapping({"/", "/login"})
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

    
    // /* ALREADY REGISTERED USER LOGIN */

    // /**
    //  * Handles the login process for a user.
    //  * Validates the user's credentials, setting the login status and session if successful.
    //  * If the credentials are invalid, redirects back with an error message.
    //  * 
    //  * @param username The username of the user attempting to log in
    //  * @param password The password of the user
    //  * @param redirectAttributes The redirect attributes to store any error messages
    //  * @param session The session to store the user data in
    //  * @return The view name to redirect to upon successful or failed login
    //  */

    // @PostMapping("/login")
    // public String processLogin(@RequestParam String username, @RequestParam String password, RedirectAttributes redirectAttributes,
    // HttpSession session) {        
    //     try {

    //         // Find user by nickname in the database
    //         Utente utente = utenteService.findByNickname(session, username).orElseThrow(() -> new IllegalArgumentException("Credenziali non valide"));
                                         
    //         /* Compares the password with the one in the database, but as we are using SpringBootSecurity and BCrypt
    //         we must use the passwordEncoder.matches(password, utente.getPassword()) to check the credentials */         
    //         if (!passwordEncoder.matches(password, utente.getPassword())) {
    //             throw new IllegalArgumentException("Credenziali non valide");
    //         }
            
    //         // Check if the user is already logged in
    //         if (utente.isLoggedIn()) {
    //             throw new IllegalArgumentException("Utente gia loggato");
    //         }

    //         // Set the login status in the database
    //         utenteService.cambiaStatusLogin(utente.getIdUtente(), true); 
    //         session.setAttribute("currentUser", utente);

    //         return "redirect:/";   
    //     } catch (IllegalArgumentException e) {
    //         redirectAttributes.addFlashAttribute("loginError", e.getMessage());
    //         return "redirect:/";
    //     }
    // }
        
    // // User logout (manual, no use of SpringBootSecurity)
    // @PostMapping("/logout")
    // public String logout(HttpSession session) {
    //     session.invalidate(); // Invalidate session
    //     return "redirect:/?logout=true";    // Redirect to the homepage
    // }

}
