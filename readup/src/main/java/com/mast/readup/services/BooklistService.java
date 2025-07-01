package com.mast.readup.services;

import com.mast.readup.entities.Booklist;
import com.mast.readup.entities.Libro;
import java.util.List;
import java.util.Optional;

public interface BooklistService {
   List<Booklist> getAllBooklistsByUser(String nickname); // Rinomino per chiarezza
    
    // findById ora restituisce direttamente Booklist (lancia eccezione se non trovata)
    Booklist findById(long idBooklist); 

    // creaBooklist prende idUtenteCreatore e Optional<String>
    Booklist creaBooklist(Long idUtenteCreatore, String nomeBooklist, String description, Optional<String> initialBookTitle);
    
    void eliminaBooklist(long idBooklist);

    // Unifichiamo i metodi di aggiunta in uno solo, che restituisce Booklist e include idUtente per sicurezza
    Booklist addBookToBooklist(long idBooklist, long idLibro, long idUtente) throws IllegalArgumentException, SecurityException;

    void removeBookFromBooklist(long idBooklist, long idLibro, long idUtente);
    
    List<Libro> getBooksInBooklist(long idBooklist);

    // Metodi per la ricerca di libri se LibroService non gestisce (meglio che LibroService gestisca la ricerca)
    //Optional<Libro> findLibroByTitolo(String titolo);
}