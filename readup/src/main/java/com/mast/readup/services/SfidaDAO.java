package com.mast.readup.services;

import java.sql.Date;
import java.util.List;

public interface SfidaDAO {
    void aggiungiSfida(int codiceSfida, String nome, String descrizione, Date inizio, Date fine);
    void rimuoviSfida(int codiceSfida);
    void aggiornaSfida(int codiceSfida, String nome, String descrizione, Date inizio, Date fine);
    void partecipaSfida(int codiceSfida, int idUtente);
    void annullaPartecipaSfida(int codiceSfida, int idUtente);
    List<String> getDatiSfida(int codiceSfida);
}
