package com.mast.readup.entities;

public class Booklist {
    private int idBooklist;
    private String nome;
    private String descrizione;
    private int idUtenteCreatore;

    public Booklist() {}

    public Booklist(int idBooklist, String nome, String descrizione, int idUtenteCreatore) {
        this.idBooklist = idBooklist;
        this.nome = nome;
        this.descrizione = descrizione;
        this.idUtenteCreatore = idUtenteCreatore;
    }

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

