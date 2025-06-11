package com.mast.readup.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Embeddable class representing the composite key for the Libreria entity.
 * Combines the IDs of a book and a user.
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LibreriaKey implements Serializable {

    // ID of the book
    @Column(name = "id_libro")
    private Long idLibro;

    // ID of the user
    @Column(name = "id_utente")
    private Long idUtente;
}