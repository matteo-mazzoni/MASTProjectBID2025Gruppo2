package com.mast.readup.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "booklist")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booklist {

    // Primary key class Booklist
    @Id
    private long idBooklist;

    // Add the extra field of the entity
    private String nome;
    private String descrizione;
    private int idUtenteCreatore;
}