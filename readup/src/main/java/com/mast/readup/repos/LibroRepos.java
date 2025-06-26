package com.mast.readup.repos;

import com.mast.readup.entities.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LibroRepos extends JpaRepository<Libro, Long> {
    /**
     * Looks for all the books have a null ISBN or an empty string in database.
     * I use an explicit SQL Query as Springboot doesn't support "Is null or empty"
     */
    @Query("SELECT l FROM Libro l WHERE l.isbn IS NULL OR l.isbn = ''")
    List<Libro> findLibriSenzaIsbn();
}
