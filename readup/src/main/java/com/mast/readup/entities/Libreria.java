package com.mast.readup.entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;   
import jakarta.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing the "libreria" table, which links users and books.
 * It models a many-to-many relationship with additional attribute 'statoPrestito'.
 */
@Entity
@Table(name = "libreria")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Libreria {

    // Composite primary key for the Libreria entity (book + user)
    @EmbeddedId
    private LibreriaKey id;

    // Status of the loan (e.g., borrowed, returned, etc.)
    @Column(name = "stato_prestito")
    private String statoPrestito;

    // Associated book (part of the composite key)
    @MapsId("idLibro")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_libro")
    private Libro libro;

    // Associated user (part of the composite key)
    @MapsId("idUtente")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utente")
    private Utente utente;

    /**
     * Constructor to initialize a Libreria instance using a book, a user, and the loan status.
     * Automatically generates the composite key.
     */
    public Libreria(Libro libro, Utente utente, String statoPrestito) {
        this.libro = libro;
        this.utente = utente;
        this.id = new LibreriaKey(libro.getIdLibro(), utente.getIdUtente());
        this.statoPrestito = statoPrestito;
    }
}