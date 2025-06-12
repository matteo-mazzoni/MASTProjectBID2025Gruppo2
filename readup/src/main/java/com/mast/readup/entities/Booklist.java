package com.mast.readup.entities;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
    @Column(name = "nome", length = 30, nullable = true)
    private String nome;

    // Booklist description
    @Column(name = "descrizione", length = 400, nullable = true)
    private String descrizione;

    // Creator of the booklist (foreign key referencing Utente)
    @ManyToOne
    @JoinColumn(name = "id_utente_creatore", nullable = true)
    private Utente utenteCreatore;

    // JSON field as a list of Strings
    @Column(name = "lista_libri", columnDefinition = "json", nullable = true)
    @Convert(converter = JsonStringListConverter.class)
    private List<String> listaLibri;
}