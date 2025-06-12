package com.mast.readup.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "booklist")
public class Booklist {

    // Primary key class Booklist
    @Id
    private int idBooklist;

    // Add the extra field of the entity
    private String nome;
    private String descrizione;
    private int idUtenteCreatore;

    // Basic costructor
    public Booklist() {
    }

    // Costructor with parameter
    public Booklist(int idBooklist, String nome, String descrizione, int idUtenteCreatore) {
        this.idBooklist = idBooklist;
        this.nome = nome;
        this.descrizione = descrizione;
        this.idUtenteCreatore = idUtenteCreatore;
    }

    // Getters and Setters
    public int getIdBooklist() {
        return idBooklist;
    }

    public void setIdBooklist(int idBooklist) {
        this.idBooklist = idBooklist;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public int getIdUtenteCreatore() {
        return idUtenteCreatore;
    }

    public void setIdUtenteCreatore(int idUtenteCreatore) {
        this.idUtenteCreatore = idUtenteCreatore;
    }
}
