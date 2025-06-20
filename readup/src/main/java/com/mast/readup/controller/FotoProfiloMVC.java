package com.mast.readup.controller;

import com.mast.readup.services.UtenteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@Controller
@RequestMapping("/profile")
public class FotoProfiloMVC {

    @Autowired
    private UtenteService utenteService;

    @PostMapping("/upload")
    public String handleImageUpload(@RequestParam("image") MultipartFile file, Principal principal) {
        if (principal == null || principal.getName() == null) {
            return "redirect:/login?error=not_authenticated";
        }
        utenteService.saveProfileImage(principal.getName(), file);
        return "redirect:/profile";
    }

    @GetMapping("/image/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable("id") Long idUtente) {
        byte[] image = utenteService.getProfileImage(idUtente);
        if (image != null) {
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(image);
        } else {
            return ResponseEntity.notFound().build(); // image not found
        }
    }
}