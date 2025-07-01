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
 * It uses Lombok to reduce boilerplate code and JPA annotations for ORM mapping.
 */

@Entity
@Table(name = "booklist")
@Data
@NoArgsConstructor // Lombok: Generates a no-argument constructor (required by JPA)
@AllArgsConstructor // Lombok: Generates a constructor with all fields
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Lombok: Limits equality to specified fields
public class Booklist {

    // Primary key of the Booklist entity
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_booklist")
    @EqualsAndHashCode.Include
    private long idBooklist;

    // Name of the booklist
    @Column(name = "nome", length = 30, nullable = true)
    private String nome;

    // Description of the booklist
    @Column(name = "descrizione", length = 400, nullable = true)
    private String descrizione;

    // Creator of the booklist (foreign key to Utente)
    @ManyToOne
    @JoinColumn(name = "id_utente_creatore", nullable = true)
    private Utente utenteCreatore;

    // Many-to-many relationship with Libro
    @ManyToMany
    @JoinTable(
        name = "booklist_contiene", 
        joinColumns = @JoinColumn(name = "id_booklist"),
        inverseJoinColumns = @JoinColumn(name = "id_libro")
    )
    private Set<Libro> libri = new HashSet<>();

    // Custom constructor for initial creation
    public Booklist(String nome, String descrizione, Utente utenteCreatore) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.utenteCreatore = utenteCreatore;
    }

    public void addLibro(Libro libro) {
        this.libri.add(libro);
    }

    public void removeLibro(Libro libro) {
        this.libri.remove(libro);
    }

    public String getDescription() {
        return descrizione;
    }

    public void setDescription(String description) {
        this.descrizione = description;
    }
}
