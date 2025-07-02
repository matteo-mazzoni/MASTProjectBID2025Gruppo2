package com.mast.readup.integration;

import com.mast.readup.entities.Libro;
import com.mast.readup.services.LibriUtenteService;
import com.mast.readup.services.UtenteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/user/books")
public class UserBookApiController {

    @Autowired
    private final LibriUtenteService libriUtenteService;
    private final UtenteService      utenteService;

    /**
     * Constructor injection of dependencies.
     * Spring will automatically wire these beans.
     */
    public UserBookApiController(LibriUtenteService libriUtenteService, UtenteService utenteService) {                    
        this.libriUtenteService = libriUtenteService;
        this.utenteService      = utenteService;
    }

    /**
     * POST /api/user/books/add
     * Receives JSON { "bookId": 123 }
     * - principal.getName() returns the username
     * - calls service.addBookAndReturn(username, bookId)
     * - maps the returned Libro into a JSON response
     */
    @PostMapping("/add")
    public ResponseEntity<Map<String,Object>> addBook( @RequestBody Map<String,Long> payload,Principal principal) {


        // 1. Extract bookId from request payload
        Long bookId = payload.get("bookId");

        // 2. Call service to add the book based on username
        Libro added = libriUtenteService.addBookToUser(principal.getName(),bookId);
        
        // 3. Build JSON response with book details
        Map<String,Object> response = Map.of(
            "success", true,
            "book", Map.of(
                "title",    added.getTitolo(),
                "author",   added.getAutore(),
                "coverUrl", added.getCoverUrl()
            )
        );

        // 4. Return HTTP 200 OK with the response body
        return ResponseEntity.ok(response);
    }


 
}
