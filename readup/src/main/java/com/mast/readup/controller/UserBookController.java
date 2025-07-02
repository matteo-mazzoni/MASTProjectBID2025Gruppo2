package com.mast.readup.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mast.readup.entities.Libro;
import com.mast.readup.services.LibriUtenteService;
import com.mast.readup.services.UtenteService;



@Controller
public class UserBookController {

    private final LibriUtenteService libriUtenteService;
    private final UtenteService      utenteService;

    public UserBookController(LibriUtenteService l, UtenteService u) {
        this.libriUtenteService = l;
        this.utenteService      = u;
    }

    @GetMapping({"/libri", "/libri.html", "/user/books"})
    public String showMyBooks(Model model, Principal principal) {
        List<Libro> userBooks = libriUtenteService.getLibriByUsername(principal.getName());
        model.addAttribute("userBooks", userBooks);
        return "libri";   
    }

    @PostMapping("/user/books/add")
    @ResponseBody
    public Map<String,Object> addBook(Principal principal, @RequestBody Map<String,Long> body) {
        String username = principal.getName();
        Long bookId     = body.get("bookId");
        try {
           Libro added = libriUtenteService.addBookToUser(username, bookId);
           return Map.of("success", true);
        } catch (Exception e) {
           return Map.of("success", false, "message", e.getMessage());
        }
    }


    @PostMapping("/updateLetto")
    @ResponseBody
    public Map<String,Object> updateLetto(@RequestBody Map<String,Object> body) {
        Long idLibro = Long.valueOf(body.get("bookId").toString());
        boolean letto = Boolean.TRUE.equals(body.get("letto"));
        libriUtenteService.updateLetto(idLibro, letto);
        return Map.of("success", true);
    }

    @PostMapping("/remove")
    public String removeBook(Principal principal,
                             @RequestParam Long idLibro) {
        String username = principal.getName();
        libriUtenteService.removeBookFromUser(username, idLibro);
        return "redirect:/libri";
    }

}