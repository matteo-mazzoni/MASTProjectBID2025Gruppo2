package com.mast.readup.entities;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "libro")
public class Libro {

    // Primary key class Libro
    @Id
    private int idLibro;

    // Add the extra field of the entity
    private Date annoUscita;
    private int numPagine;
    private String genere;
    private String titolo;
    private String autore;
    private String casaEditrice;

    // Basic constructor
    public Libro() {
    }

    // Costructor with parameters
    public Libro(int idLibro, Date annoUscita, int numPagine, String genere, String titolo, String autore,
            String casaEditrice) {
        this.idLibro = idLibro;
        this.annoUscita = annoUscita;
        this.numPagine = numPagine;
        this.genere = genere;
        this.titolo = titolo;
        this.autore = autore;
        this.casaEditrice = casaEditrice;
    }

    // Getters and Setters
    public int getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(int idLibro) {
        this.idLibro = idLibro;
    }

    public Date getAnnoUscita() {
        return annoUscita;
    }

    public void setAnnoUscita(Date annoUscita) {
        this.annoUscita = annoUscita;
    }

    public int getNumPagine() {
        return numPagine;
    }

    public void setNumPagine(int numPagine) {
        this.numPagine = numPagine;
    }

    public String getGenere() {
        return genere;
    }

    public void setGenere(String genere) {
        this.genere = genere;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getAutore() {
        return autore;
    }

    public void setAutore(String autore) {
        this.autore = autore;
    }

    public String getCasaEditrice() {
        return casaEditrice;
    }

    public void setCasaEditrice(String casaEditrice) {
        this.casaEditrice = casaEditrice;
    }

}
