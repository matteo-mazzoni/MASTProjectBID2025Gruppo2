package com.mast.readup.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReadUpMVC {

    // Home
    @GetMapping("/")
    public String home(Model model) {
        return "index";
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