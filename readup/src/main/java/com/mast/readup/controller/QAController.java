package com.mast.readup.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller 
public class QAController {

    // Q&A page
    @GetMapping("/qa.html")
    public String qa() {
        return "qa";
    }

   

}
   