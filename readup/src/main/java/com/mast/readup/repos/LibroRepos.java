package com.mast.readup.repos;

import com.mast.readup.entities.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LibroRepos extends JpaRepository<Libro, Long> {
    /**
     * Looks for all the books have a null ISBN or an empty string in database.
     * Explicit SQL Query as Springboot doesn't support "Is null or empty"
     */
    @Query("SELECT l FROM Libro l WHERE l.isbn IS NULL OR l.isbn = ''")
    List<Libro> findLibriSenzaIsbn();

    /**
     * It return 4 random books with ISBN not null
     */
    @Query(value = "SELECT * FROM libro WHERE isbn IS NOT NULL ORDER BY RAND() LIMIT 4",  nativeQuery = true)   
    List<Libro> findRandom4WithIsbn();

    
    /**
     * Return all books with ISBN not null
     * 
     */
    List<Libro> findAllByIsbnNotNull();

}
