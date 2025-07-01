package com.mast.readup.repos;

import com.mast.readup.entities.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LibroRepos extends JpaRepository<Libro, Long> {


    // 1a) Find all books without ISBN (used for ISBN population at the application start)
    @Query("SELECT l FROM Libro l WHERE l.isbn IS NULL OR l.isbn = ''")
    List<Libro> findLibriSenzaIsbn();

    // 1b) Find all books with ISBN 
    List<Libro> findAllByIsbnNotNull();

    // 2) Find all the genres of books in the database
    @Query("SELECT DISTINCT l.genere FROM Libro l WHERE l.genere IS NOT NULL")
    List<String> findDistinctGenres();
}
