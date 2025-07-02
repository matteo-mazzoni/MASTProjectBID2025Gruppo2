package com.mast.readup.services;

import com.mast.readup.entities.Libro;
import java.util.List;

public interface LibriUtenteService {

    /**
     * Retrieves all books associated with a specific user ID.
     *
     * @param idUtente the ID of the user whose books are to be retrieved
     * @return a list of Libro objects associated with the specified user
     * @throws IllegalArgumentException if the user ID is null or if the user does not exist
     */
    List<Libro> getLibriByUtenteId(Long idUtente);

    /**
     * Adds a book to the user's collection and returns the added book.
     *
     * @param username the username of the user
     * @param bookId the ID of the book to be added
     * @return the Libro object that was added, or null if the book could not be added
     */
    Libro addBookToUser(String username, Long bookId);

    /**
     * Retrieves all books associated with a specific username.
     *
     * @param username the user’s nickname
     * @return list of Libro for that username
     * @throws IllegalArgumentException if the username is not found
     */
    List<Libro> getLibriByUsername(String username);


      /**
     * Removes a book from a user's collection.
     *
     * @param username nickname of the user
     * @param idLibro  book ID to be removed
     */
    void removeBookFromUser(String username, Long idLibro);
    
    
    
    /**
     * Updates the "letto" status of a book for a user.
     *
     * @param idLibro the ID of the book to update
     * @param letto   true if the book is marked as read, false otherwise (false is by default)
     */
    void updateLetto(Long idLibro, boolean letto);
}
   


