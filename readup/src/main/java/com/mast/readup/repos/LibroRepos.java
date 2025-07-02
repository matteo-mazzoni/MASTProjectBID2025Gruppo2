package com.mast.readup.repos;

import com.mast.readup.entities.Libro;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LibroRepos extends JpaRepository<Libro, Long> {

    // 1a) Find all books without ISBN (used for ISBN population at the application start)
    @Query("SELECT l FROM Libro l WHERE l.isbn IS NULL OR l.isbn = ''")
    List<Libro> findLibriSenzaIsbn();

    // 1b) Find all books with ISBN 
    List<Libro> findAllByIsbnNotNull();

    // 2) Find all the genres of books in the database
    @Query("SELECT DISTINCT l.genere FROM Libro l WHERE l.genere IS NOT NULL")
    List<String> findDistinctGenres();

    // Metodo per trovare un libro per titolo esatto (utile per la creazione di booklist)
    Optional<Libro> findByTitoloIgnoreCase(String titolo);

    // NUOVO/MODIFICATO: Metodo per la ricerca parziale e case-insensitive
    List<Libro> findByTitoloContainingIgnoreCase(String titolo);

    // Puoi aggiungere anche ricerca per autore se necessario
    List<Libro> findByAutoreContainingIgnoreCase(String autore);

    // Puoi combinare:
    List<Libro> findByTitoloContainingIgnoreCaseOrAutoreContainingIgnoreCase(String titolo, String autore);


     /**
     * 3) Update description of a certain book
     */
    @Modifying
    @Transactional
    @Query("UPDATE Libro l SET l.descrizione = :desc WHERE l.idLibro = :id")
    void updateDescription(@Param("id") long id, @Param("desc") String desc);
}
