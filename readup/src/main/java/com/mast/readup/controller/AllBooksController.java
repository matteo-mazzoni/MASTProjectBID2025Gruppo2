package com.mast.readup.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.ui.Model;
import com.mast.readup.entities.Libro;
import com.mast.readup.services.LibroService;
 



@Controller 
public class AllBooksController {

    @Autowired
    private final LibroService libroService;

    
    public AllBooksController(LibroService libroService) {
        this.libroService = libroService;
    }

    @GetMapping("/libriReadUp.html")
    public String qa(Model model) {
        // Retrieve all books from the service and inject them into the view
        List<Libro> libri = libroService.findAll();
        model.addAttribute("books", libri);

        List<String> genres = libroService.findAllGenres(); 
        model.addAttribute("genres", genres);
        return "allbooks";
    }
}
   