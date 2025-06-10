package com.mast.readup.entities;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sfida")
public class Sfida {

    // Primary key class Sfida
    @Id
    private int codiceSfida;

    // Add the extra field of the entity
    private String nomeSfida;
    private String descrizioneSfida;
    private int numPartecipanti;
    private Date dataInizio;
    private Date dataFine;

    // Basic constructor
    public Sfida() {
    }

    // Costructor with parameters
    public Sfida(int codiceSfida, String nomeSfida, String descrizioneSfida, int numPartecipanti, Date dataInizio,
            Date dataFine) {
        this.codiceSfida = codiceSfida;
        this.nomeSfida = nomeSfida;
        this.descrizioneSfida = descrizioneSfida;
        this.numPartecipanti = numPartecipanti;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
    }

    // Getters and Setters
    public int getCodiceSfida() {
        return codiceSfida;
    }

    public void setCodiceSfida(int codiceSfida) {
        this.codiceSfida = codiceSfida;
    }

    public String getNomeSfida() {
        return nomeSfida;
    }

    public void setNomeSfida(String nomeSfida) {
        this.nomeSfida = nomeSfida;
    }

    public String getDescrizioneSfida() {
        return descrizioneSfida;
    }

    public void setDescrizioneSfida(String descrizioneSfida) {
        this.descrizioneSfida = descrizioneSfida;
    }

    public int getNumPartecipanti() {
        return numPartecipanti;
    }

    public void setNumPartecipanti(int numPartecipanti) {
        this.numPartecipanti = numPartecipanti;
    }

    public Date getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(Date dataInizio) {
        this.dataInizio = dataInizio;
    }

    public Date getDataFine() {
        return dataFine;
    }

    public void setDataFine(Date dataFine) {
        this.dataFine = dataFine;
    }
}
