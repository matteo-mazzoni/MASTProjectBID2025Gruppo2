package com.mast.readup.entities;

import java.sql.Date;

public class Sfida {
    private int codiceSfida;
    private String nomeSfida;
    private String descrizioneSfida;
    private int numPartecipanti;
    private Date dataInizio;
    private Date dataFine;

    public Sfida() {}

    public Sfida(int codiceSfida, String nomeSfida, String descrizioneSfida, int numPartecipanti, Date dataInizio, Date dataFine) {
        this.codiceSfida = codiceSfida;
        this.nomeSfida = nomeSfida;
        this.descrizioneSfida = descrizioneSfida;
        this.numPartecipanti = numPartecipanti;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
    }

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
