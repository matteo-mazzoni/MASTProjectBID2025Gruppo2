package com.mast.readup.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "utente")
public class Utente {

    // Primary key class Utente
    @Id
    private int idUtente;

    // Add the extra field of the entity
    private String nickname;
    private String password;
    private String email;
    private String citta;
    private boolean loggedIn;

    // Basic constructor
    public Utente() {
    }

    // Costructor with parameters
    public Utente(int idUtente, String nickname, String password, String email, String citta, boolean loggedIn) {
        this.idUtente = idUtente;
        this.nickname = nickname;
        this.password = password;
        this.email = email;
        this.citta = citta;
        this.loggedIn = loggedIn;
    }

    // Getters and Setters
    public int getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(int idUtente) {
        this.idUtente = idUtente;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCitta() {
        return citta;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
}
