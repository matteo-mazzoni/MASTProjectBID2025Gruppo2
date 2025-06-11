package com.mast.readup.entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Embeddable class representing the composite key for BooklistContiene.
 * Consists of foreign key references to Libro, Booklist, and Utente.
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BooklistContieneKey implements Serializable {

    // Book ID
    private Long idLibro;

    // Booklist ID
    private Long idBooklist;

    // User ID
    private Long idUtente;
}