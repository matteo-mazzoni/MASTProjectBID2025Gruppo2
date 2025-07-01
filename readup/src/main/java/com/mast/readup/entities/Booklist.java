package com.mast.readup.entities;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * This class represents the "booklist" entity in the database.
 * It uses Lombok to reduce boilerplate code and JPA annotations for ORM
 * mapping.
 */

@Entity
@Table(name = "booklist")
@Data
@NoArgsConstructor // Lombok: Generates a no-argument constructor (required by JPA)
@AllArgsConstructor // Lombok: Generates a constructor with all fields
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Lombok: Limits equality to specified fields
public class Booklist {

    // Primary key class Booklist
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_booklist")
    private long idBooklist;

    // Add the extra field of the entity
    // Booklist name
    @EqualsAndHashCode.Include 
    @Column(name = "nome", length = 30, nullable = true)
    private String nome;

    // Booklist description
    @Column(name = "descrizione", length = 400, nullable = true)
    private String descrizione;

    // Creator of the booklist (foreign key referencing Utente)
    @ManyToOne
    @JoinColumn(name = "id_utente_creatore", nullable = true)
    private Utente utenteCreatore;

    @ManyToMany // Una booklist può avere molti libri, un libro può essere in molte booklist
    @JoinTable(
        name = "booklist_libri", 
        joinColumns = @JoinColumn(name = "id_booklist"),
        inverseJoinColumns = @JoinColumn(name = "id_libro")
    )
    private Set<Libro> libri = new HashSet<>();

    public String getName() {
        return nome;
    }

    // Se l'errore menziona anche utenteCreatore dopo aver risolto name
    public Utente getUtenteCreatore() {
        return utenteCreatore;
    }
    public void addLibro(Libro libro) {
        this.libri.add(libro);
        if (libro != null) { // Aggiunto controllo null per sicurezza
            libro.getBooklists().add(this);
        }
    }

    public void removeLibro(Libro libro) {
        this.libri.remove(libro);
        if (libro != null) { // Aggiunto controllo null per sicurezza
            libro.getBooklists().remove(this);
        }
    }
}
