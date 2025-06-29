package com.mast.readup.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserBookController {

     // I miei libri
    @GetMapping("/libri.html")
    public String libri(){
        return "book";
    }

}
