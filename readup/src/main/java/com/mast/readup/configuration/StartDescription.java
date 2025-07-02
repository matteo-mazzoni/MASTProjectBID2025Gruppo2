package com.mast.readup.configuration;

import com.mast.readup.entities.Libro;
import com.mast.readup.integration.GoogleBooksClient;
import com.mast.readup.repos.LibroRepos;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StartDescription {

    /**
     * On application startup, fetch descriptions for books missing them
     * and save updates back to the database.
     */
    @Bean
    public CommandLineRunner descriptionUpdater(LibroRepos libroRepo, GoogleBooksClient googleClient) {
        return args -> {
            for (Libro book : libroRepo.findAll()) {
                if (book.getDescrizione() == null || book.getDescrizione().isBlank()) {
                String desc;
                try {
                    
                    // 1) Try by ISBN if present
                    String isbn = book.getIsbn();
                    if (isbn != null && !isbn.isBlank()) {
                        desc = googleClient.fetchDescriptionByIsbn(isbn).orElseGet(() ->

                        // 2) Fallback title+author
                        googleClient.fetchDescription(book.getTitolo(), book.getAutore()).orElse("Descrizione in arrivo..."));             
                    } else {
                    
                        // 2. No ISBN: go straight to title+author
                        desc = googleClient.fetchDescription(book.getTitolo(), book.getAutore()).orElse("Descrizione in arrivo...");           
                    }
                
                } catch (RequestNotPermitted ex) {
                    // 3. Rate limit hit, log and set placeholder
                    desc = "Descrizione in arrivo...";        
                    
                    // 4. Log the error in fetching description and sets placeholder
                } catch (Exception ex) {
                    System.err.println("Error fetching description for '" + book.getTitolo() + "' by " + book.getAutore() + ": " + ex.getMessage()); 
                    desc = "Descrizione in arrivo...";
                }

                // Update book description in database and save
                book.setDescrizione(desc);
                 libroRepo.updateDescription(book.getIdLibro(), desc);
               
            }}
        
        };

        
    }
}
    

