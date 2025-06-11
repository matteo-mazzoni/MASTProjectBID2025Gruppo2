package com.mast.readup.entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing the "booklist_contiene" table.
 * Models a many-to-many relationship between Booklist and Libro,
 * associated with a specific Utente, using a composite key.
 */
@Entity
@Table(name = "booklist_contiene")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BooklistContiene {

    // Composite primary key (book + booklist + user)
    @EmbeddedId
    private BooklistContieneKey id;

    // Associated book (part of composite key)
    @MapsId("idLibro")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_libro")
    private Libro libro;

    // Associated booklist (part of composite key)
    @MapsId("idBooklist")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_booklist")
    private Booklist booklist;

    // Associated user (part of composite key)
    @MapsId("idUtente")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utente")
    private Utente utente;
}