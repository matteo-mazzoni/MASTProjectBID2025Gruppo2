package com.mast.readup.services;

import com.mast.readup.entities.Utente;

public interface UtenteDAO {
    void aggiungiUtente(String nickname, String password, String email, String citta);
    void modificaPassword(int idUtente, String nuovaPassword);
    void modificaMail(int idUtente, String nuovaEmail);
    void modificaNickname(int idUtente, String nuovoNickname);
    void modificaCitta(int idUtente, String nuovaCitta);
    void cambiaStatusLogin(int idUtente, boolean loggedIn);
    void eliminaUtente(int idUtente);
}

