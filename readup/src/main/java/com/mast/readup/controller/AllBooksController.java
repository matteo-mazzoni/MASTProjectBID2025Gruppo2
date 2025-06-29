package com.mast.readup.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller 
public class AllBooksController {

    // Q&A page
    @GetMapping("/libriReadUp.html")
    public String qa() {
        return "allbooks";
    }

   

}
   