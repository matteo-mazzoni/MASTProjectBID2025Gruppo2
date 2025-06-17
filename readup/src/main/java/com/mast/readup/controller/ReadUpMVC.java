package com.mast.readup.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReadUpMVC {

    @GetMapping("/")
    public String home(Model model) {
        return "index"; 
    }

    @GetMapping("/profilo.html")
    public String profilo(Model model) {
        return "profilo"; 
    }

    @GetMapping("/booklist.html")
    public String booklist(Model model) {
        return "booklist"; 
    }

    @GetMapping("/sfide.html")
    public String sfide(Model model) {
        return "sfide"; 
    }

    @GetMapping("/faq.html")
    public String faq() {
        return "faq"; 
    }

    @GetMapping("/contattaci.html")
    public String contattaci(Model model) {
        return "contattaci"; 
    }

}