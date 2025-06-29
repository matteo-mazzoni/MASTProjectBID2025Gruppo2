// package com.mast.readup.controller;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.core.annotation.AuthenticationPrincipal;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;

// import com.mast.readup.entities.Utente;
// import com.mast.readup.services.LibroService;
// import com.mast.readup.services.UtenteService;

// @Controller
// public class HomeController {

//     @Autowired
//     private UtenteService utenteService;

//     @Autowired
//     private LibroService libroService;

    
//     @GetMapping("/home.html")
//     public String home(Model model,  @AuthenticationPrincipal UserDetails userDetails) {

//         // retrieves the logged user
//         Utente currentUser = utenteService.findByNickname(userDetails.getUsername()).orElseThrow();
                                
//         model.addAttribute("currentUser", currentUser);


//         if (!model.containsAttribute("Utente")) {
//             model.addAttribute("Utente", new Utente());
//             model.addAttribute("successMessage", null);                          
//             model.addAttribute("currentUser", currentUser);      
//         }
//         var carousel = libroService.findRandomCarousel(4);
//         model.addAttribute("libri", carousel);
//         return "home";
//     }
// }