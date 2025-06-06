package com.mast.readup.services;

import java.util.List;

public interface BooklistDAO {
    void creaBooklist(String nome, String descrizione, int idUtenteCreatore);
    void modificadatiBooklist(int idBooklist, String nuovoNome, String nuovaDescrizione);
    void modificaLibriBooklist(int idBooklist, List<String> nuoviLibri);
    void eliminaBooklist(int idBooklist);
}
