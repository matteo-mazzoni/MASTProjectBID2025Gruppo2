package com.mast.readup.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.mast.readup.entities.Utente;
import com.mast.readup.services.LibroService;

@Controller
public class HomeController {

    @Autowired
    private LibroService libroService;

    @GetMapping("/")
    public String home(Model model) {
        if (!model.containsAttribute("Utente")) {
            model.addAttribute("Utente", new Utente());
        }
        var carousel = libroService.findRandomCarousel(4);
        model.addAttribute("libri", carousel);
        return "index";
    }
}